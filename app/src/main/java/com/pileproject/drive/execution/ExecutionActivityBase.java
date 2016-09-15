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
import com.pileproject.drive.util.fragment.AlertDialogFragment;
import com.pileproject.drive.util.fragment.ProgressDialogFragment;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ExecutionSpaceManager;

/**
 * An Activity that deals with executions of programs.
 */
public abstract class ExecutionActivityBase extends AppCompatActivity implements AlertDialogFragment.EventListener {
    private static final int DIALOG_REQUEST_CODE_CONNECTION_ATTEMPT_FAILED = 1;
    private static final int DIALOG_REQUEST_CODE_THREAD_ENDED              = 2;
    private static final int DIALOG_REQUEST_CODE_CONNECTION_ERROR          = 3;
    private static final int DIALOG_REQUEST_CODE_BLUETOOTH                 = 4;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int FAILED_TO_CONNECT = 1;

    private BluetoothAdapter mBtAdapter = null;
    private ExecutionSpaceManager mSpaceManager;

    private Button mStopAndRestartButton;
    private Button mFinishButton;
    private ExecutionThread mThread = null;

    // Handler for connecting to a device
    private final Handler mConnectingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case FAILED_TO_CONNECT:
                    onConnectionAttemptFailed();
                    return true;
            }
            return false;
        }
    });

    // Handler for showing the progress of executions
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
                    // inform the error of the thread
                    onConnectionError();
                    return true;
                }
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution);

        findViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.execution_toolbar);
        toolbar.setLogo(R.drawable.icon_launcher);
        setSupportActionBar(toolbar);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

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

        // check this activity has already connected to the device
        Intent intent = getIntent();
        if (intent.getBooleanExtra(getString(R.string.key_execution_isConnected), false)) {
            setResult(Activity.RESULT_OK, intent);
            try {
                startExecution();
            } catch (RuntimeException e) {
                e.printStackTrace();
                onConnectionError();
            }
        } else {
            if (hasBluetoothFunction()) {
                if (!mBtAdapter.isEnabled()) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                                           REQUEST_ENABLE_BT);
                } else {
                    connectToDevice();
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    protected abstract void connectToDevice();

    @Override
    protected void onStop() {
        super.onStop();
        finishExecution();
    }

    @Override
    public void onBackPressed() {
        finishExecution();
    }

    private void findViews() {
        mSpaceManager =
                new ExecutionSpaceManager(this, (BlockSpaceLayout) findViewById(R.id.execute_showingProgressLayout));
        mStopAndRestartButton = (Button) findViewById(R.id.execute_stopAndRestartButton);
        mFinishButton = (Button) findViewById(R.id.execute_finishButton);
    }

    protected void startExecution() {
        if (mThread != null && mThread.isAlive()) return;

        mThread = new ExecutionThread(mProgressHandler, getMachineController());
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

    private boolean hasBluetoothFunction() {
        // check this device has bluetooth connecting
        if (mBtAdapter == null) {
            new AlertDialogFragment.Builder(this)
                    .setRequestCode(DIALOG_REQUEST_CODE_BLUETOOTH)
                    .setTitle(R.string.error)
                    .setMessage(R.string.execute_noBluetoothFunction)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .setCancelable(false)
                    .show();
            return false;
        }
        return true;
    }

    protected void showConnectionProgressDialog() {
        ProgressDialogFragment.showDialog(getSupportFragmentManager(),
                getString(R.string.execute_connecting), getString(R.string.execute_pleaseWaitForAWhile), "tag");
    }

    protected void dismissConnectionProgressDialog() {
        ProgressDialogFragment.dismissDialog();
    }

    protected void showConnectionFailedDialog() {
        mConnectingHandler.sendEmptyMessage(FAILED_TO_CONNECT);
    }

    protected abstract MachineController getMachineController();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getBaseContext(), R.string.turnedOnBluetooth, Toast.LENGTH_SHORT).show();
                    connectToDevice();
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

    private void onConnectionAttemptFailed() {
         new AlertDialogFragment.Builder(this)
                .setRequestCode(DIALOG_REQUEST_CODE_CONNECTION_ATTEMPT_FAILED)
                .setMessage(R.string.execute_bluetoothConnectionError)
                .setPositiveButtonLabel(android.R.string.ok)
                .show();
    }

    private void onThreadEnded() {
        new AlertDialogFragment.Builder(this)
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

        // inform this device has been disconnected by the device
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.key_execution_isConnected), false);
        setResult(RESULT_OK, intent);
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

    }
}
