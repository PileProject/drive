/**
 * Copyright (C) 2011-2016 PILE Project, Inc. <dev@pileproject.com>
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
import com.pileproject.drive.database.ProgramDataManager;
import com.pileproject.drive.machine.MachineProvider;
import com.pileproject.drive.util.bluetooth.BluetoothUtil;
import com.pileproject.drive.util.development.DeployUtils;
import com.pileproject.drive.util.fragment.AlertDialogFragment;
import com.pileproject.drive.util.fragment.ProgressDialogFragment;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ExecutionSpaceManager;
import com.pileproject.drivecommand.machine.MachineBase;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    private RxObservableProgram mObservableProgram;

    private MachineBase mMachine;

    @Inject
    public MachineProvider mMachineProvider;

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

        mObservableProgram = new RxObservableProgram(
                ProgramDataManager.getInstance().loadExecutionProgram(),
                mMachineProvider.getMachineController(mMachine));

        mSpaceManager.loadExecutionProgram();

        mStopAndRestartButton.setOnClickListener(pauser);

        findViewById(R.id.execute_finishButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateExecution();
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

        // if this activity comes back directly (not via ProgrammingActivity),
        // make this activity finish so that users can start at the programming screen immediately

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // this activity will be shut down soon if onRestart is called.
        // so it's safe to call these here (no field members are reused)
        // note that we have fragments so we cannot do these in onPause

        terminateExecution();
        mSubscriptions.unsubscribe();
    }

    private void inject() {
        ((DriveApplication) getApplication()).getAppComponent().inject(this);
    }

    private void findViews() {
        mSpaceManager = new ExecutionSpaceManager(this, (BlockSpaceLayout) findViewById(R.id.execute_showingProgressLayout));
        mStopAndRestartButton = (Button) findViewById(R.id.execute_stopAndRestartButton);
    }

    private void startExecution() {
        mSubscriptions.add(
            Observable.create(mObservableProgram)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Bundle>() {
                    @Override
                    public void onCompleted() {
                        new AlertDialogFragment.Builder(ExecutionActivity.this)
                            .setRequestCode(DIALOG_REQUEST_CODE_THREAD_ENDED)
                            .setMessage(R.string.execute_showNoteOfPort)
                            .setPositiveButtonLabel(android.R.string.ok)
                            .setCancelable(false)
                            .setWindowGravity(Gravity.BOTTOM)
                            .setAllowingStateLoss(true)
                            .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        new AlertDialogFragment.Builder(ExecutionActivity.this)
                            .setRequestCode(DIALOG_REQUEST_CODE_CONNECTION_ERROR)
                            .setTitle(R.string.error)
                            .setMessage(R.string.execute_disconnectedByNXT)
                            .setPositiveButtonLabel(android.R.string.ok)
                            .setCancelable(false)
                            .setAllowingStateLoss(true)
                            .show();
                    }

                    @Override
                    public void onNext(Bundle bundle) {

                        int message = bundle.getInt(RxObservableProgram.KEY_MESSAGE_TYPE);

                        switch (message) {
                            case RxObservableProgram.MESSAGE_STARTED: {
                                Toast.makeText(getBaseContext(), R.string.execute_startExecution, Toast.LENGTH_SHORT).show();
                                break;
                            }

                            case RxObservableProgram.MESSAGE_BLOCK_EXECUTED: {
                                int index = bundle.getInt(RxObservableProgram.KEY_MESSAGE_ARG);
                                mSpaceManager.emphasizeBlock(index);
                                break;
                            }
                        }
                    }
                }));
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
                                .setAllowingStateLoss(true)
                                .show();
                    }

                    @Override
                    public void onNext(MachineBase machineBase) {
                        // no-op
                    }
                })
        );
    }

    private void terminateExecution() {
        mObservableProgram.requestTerminate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != REQUEST_ENABLE_BT) {
            return ;
        }

        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(getBaseContext(), R.string.turnedOnBluetooth, Toast.LENGTH_SHORT).show();
            connectToMachine();

        } else {
            new AlertDialogFragment.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.bluetoothFunctionIsOff)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .show();
        }
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

    private final OnClickListener pauser = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mObservableProgram.requestPause();

            mStopAndRestartButton.setText(R.string.execute_restart);
            mStopAndRestartButton.setOnClickListener(restarter);
        }
    };

    private final OnClickListener restarter = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mObservableProgram.requestRestart();

            mStopAndRestartButton.setText(R.string.execute_stop);
            mStopAndRestartButton.setOnClickListener(pauser);
        }
    };
}
