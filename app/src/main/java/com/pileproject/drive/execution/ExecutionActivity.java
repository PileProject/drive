/*
 * Copyright (C) 2011-2015 PILE Project, Inc. <dev@pileproject.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pileproject.drive.execution;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pileproject.drive.R;
import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.comm.BluetoothCommunicatorProvider;
import com.pileproject.drive.comm.CommunicatorProvider;
import com.pileproject.drive.comm.RxMachineConnector;
import com.pileproject.drive.util.bluetooth.BluetoothUtil;
import com.pileproject.drive.util.development.DeployUtils;
import com.pileproject.drive.util.fragment.AlertDialogFragment;
import com.pileproject.drive.util.fragment.ProgressDialogFragment;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ExecutionSpaceManager;
import com.pileproject.drivecommand.machine.MachineBase;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * An Activity that deals with executions of programs.
 */
public class ExecutionActivity extends AppCompatActivity implements AlertDialogFragment.EventListener {
    private static final int DIALOG_REQUEST_CODE_CONNECTION_ATTEMPT_FAILED = 1;
    private static final int DIALOG_REQUEST_CODE_THREAD_ENDED              = 2;
    private static final int DIALOG_REQUEST_CODE_CONNECTION_ERROR          = 3;
    private static final int DIALOG_REQUEST_CODE_BLUETOOTH                 = 4;

    private static final int REQUEST_ENABLE_BT = 1;

    private ExecutionSpaceManager mSpaceManager;

    private Button mStopAndRestartButton;
    private Button mFinishButton;
    private ExecutionThread mThread = null;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    private MachineBase mMachine;

    @Inject
    public MachineProvider mMachineProvider;

    // a handler for showing the progress of executions
    private final Handler mProgressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.getData().getInt(getString(R.string.key_execution_message))) {
                case ExecutionThread.START_THREAD: {
                    Toast.makeText(getBaseContext(), R.string.execute_startExecution, Toast.LENGTH_SHORT).show();
                    return true;
                }

                case ExecutionThread.EMPHASIZE_BLOCK: {
                    // emphasize the current executing block
                    int index = message.getData().getInt(getString(R.string.key_execution_index));
                    mSpaceManager.emphasizeBlock(index);
                    return true;
                }

                case ExecutionThread.END_THREAD: {
                    onThreadEnded();
                    return true;
                }

                case ExecutionThread.CONNECTION_ERROR: {
                    // handle connection errors
                    onConnectionError();
                    return true;
                }
            }
            return false;
        }
    });

    /**
     * Returns an intent for invoking {@link ExecutionActivity}.
     *
     * @param context context to be passed to the constructor of {@link Intent}.
     * @return intent
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ExecutionActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution);

        inject();

        findViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.execution_toolbar);
        toolbar.setLogo(R.drawable.icon_launcher);
        setSupportActionBar(toolbar);

        CommunicatorProvider communicatorProvider = BluetoothCommunicatorProvider.getInstance();

        mMachine = mMachineProvider.getMachine(communicatorProvider.getCommunicator());

        mSpaceManager.loadExecutionProgram();

        mStopAndRestartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopExecution();
            }
        });

        mFinishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishExecution();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: remove bluetooth-dependent code for WiFiCommunicator
        if (!DeployUtils.isOnEmulator() && !BluetoothUtil.hasBluetoothFunction()) {
            new AlertDialogFragment.Builder(this)
                    .setRequestCode(DIALOG_REQUEST_CODE_BLUETOOTH)
                    .setTitle(R.string.error)
                    .setMessage(R.string.execute_noBluetoothFunction)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .setCancelable(false)
                    .show();

            return;
        }

        if (!DeployUtils.isOnEmulator() && !BluetoothUtil.isBluetoothEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            return;
        }

        connectToMachine();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishExecution();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSubscriptions.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        finishExecution();
    }

    private void inject() {
        ((DriveApplication) getApplication()).getAppComponent().inject(this);
    }

    private void findViews() {
        mSpaceManager = new ExecutionSpaceManager(this, (BlockSpaceLayout) findViewById(R.id.execute_showingProgressLayout));
        mStopAndRestartButton = (Button) findViewById(R.id.execute_stopAndRestartButton);
        mFinishButton = (Button) findViewById(R.id.execute_finishButton);
    }

    private void startExecution() {
        if (mThread != null && mThread.isAlive()) return;

        mThread = new ExecutionThread(mProgressHandler, mMachineProvider.getMachineController(mMachine));
        mThread.setPriority(Thread.MAX_PRIORITY);
        mThread.start();
    }

    private void stopExecution() {
        if (mThread == null || !mThread.isAlive()) return ;

        mThread.stopExecution();
        mStopAndRestartButton.setText(R.string.execute_restart);
        mStopAndRestartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                restartExecution();
            }
        });
    }

    private void restartExecution() {
        mThread.restartExecution();
        mStopAndRestartButton.setText(R.string.execute_stop);
        mStopAndRestartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopExecution();
            }
        });
    }

    private void finishExecution() {
        if (mThread == null || !mThread.isAlive()) return ;

        mThread.haltExecution();
        waitForThreadToBeOver();
    }

    private void waitForThreadToBeOver() {
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getBaseContext(), R.string.turnedOnBluetooth, Toast.LENGTH_SHORT).show();
                    connectToMachine();
                    break;
                }

                new AlertDialogFragment.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.bluetoothFunctionIsOff)
                        .setPositiveButtonLabel(android.R.string.ok)
                        .show();
                break;
        }
    }

    private void onThreadEnded() {
        new AlertDialogFragment.Builder(this)
                .setRequestCode(DIALOG_REQUEST_CODE_THREAD_ENDED)
                .setMessage(R.string.execute_showNoteOfPort)
                .setPositiveButtonLabel(android.R.string.ok)
                .setCancelable(false)
                .setWindowGravity(Gravity.BOTTOM)
                .show();
    }

    private void onConnectionError() {
        new AlertDialogFragment.Builder(this)
                .setRequestCode(DIALOG_REQUEST_CODE_CONNECTION_ERROR)
                .setTitle(R.string.error)
                .setMessage(R.string.execute_disconnectedByNXT)
                .setPositiveButtonLabel(android.R.string.ok)
                .setCancelable(false)
                .show();
    }

    private void connectToMachine() {

        // create a ProgressDialog
        ProgressDialogFragment.showDialog(getSupportFragmentManager(),
                getString(R.string.execute_connecting),
                getString(R.string.execute_pleaseWaitForAWhile), "");

        mSubscriptions.add(
            RxMachineConnector.connect(mMachine)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MachineBase>() {

                    @Override
                    public void onCompleted() {
                        ProgressDialogFragment.dismissDialog();
                        startExecution();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressDialogFragment.dismissDialog();

                        new AlertDialogFragment.Builder(ExecutionActivity.this)
                                .setRequestCode(DIALOG_REQUEST_CODE_CONNECTION_ATTEMPT_FAILED)
                                .setMessage(R.string.execute_bluetoothConnectionError)
                                .setPositiveButtonLabel(android.R.string.ok)
                                .show();
                    }

                    @Override
                    public void onNext(MachineBase machineBase) {
                        // no-op
                    }
                })
        );
    }

    @Override
    public void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params) {
        switch (requestCode) {
            case DIALOG_REQUEST_CODE_CONNECTION_ATTEMPT_FAILED:
            case DIALOG_REQUEST_CODE_THREAD_ENDED:
            case DIALOG_REQUEST_CODE_CONNECTION_ERROR:
            case DIALOG_REQUEST_CODE_BLUETOOTH:
                finish();
                break;
        }
    }

    @Override
    public void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params) {
        // no-op
    }
}
