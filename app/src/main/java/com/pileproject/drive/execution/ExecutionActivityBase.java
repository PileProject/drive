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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ProgressSpaceManager;

/**
 * this Activity shows the execution of program
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public abstract class ExecutionActivityBase extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int FAILED_TO_CONNECT = 1;
    @SuppressWarnings("unused")
    private static final String TAG = "NxtExecutionActivity";
    // Handler for connecting
    private final Handler mConnectingHandler =
            new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.what) {
                        case FAILED_TO_CONNECT:
                            // Show error and finish
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    ExecutionActivityBase.this).setTitle(R.string.error)
                                    .setMessage(R.string.execute_bluetoothConnectionError)
                                    .setPositiveButton(R.string.ok,
                                                       new DialogInterface
                                                               .OnClickListener() {
                                                           @Override
                                                           public void onClick(
                                                                   DialogInterface dialog,
                                                                   int which) {
                                                               finish();
                                                           }
                                                       })
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            return true;
                    }
                    return false;
                }
            });
    private BluetoothAdapter mBtAdapter = null;
    private ProgressDialog mProgressDialog;
    private ProgressSpaceManager mSpaceManager;
    // Handler for showing the progress of executions
    private final Handler mProgressHandler =
            new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    switch (message.getData().getInt("message")) {
                        case ExecutionThread.START_THREAD: {
                            Toast.makeText(getBaseContext(),
                                           R.string.execute_startExecution,
                                           Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        case ExecutionThread.EMPHASIZE_BLOCK: {
                            // Emphasize the current executing block
                            int index = message.getData().getInt("index");
                            mSpaceManager.emphasizeBlock(index);
                            return true;
                        }

                        case ExecutionThread.END_THREAD: {
                            // Inform the end of the thread
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    ExecutionActivityBase.this).setTitle(R.string.execute_executionIsOver)
                                    .setMessage(R.string.execute_showNoteOfPort)
                                    .setPositiveButton(R.string.ok,
                                                       new DialogInterface
                                                               .OnClickListener() {
                                                           @Override
                                                           public void onClick(
                                                                   DialogInterface dialog,
                                                                   int which) {
                                                               finish();
                                                           }
                                                       })
                                    .create();
                            // Move to bottom
                            alertDialog.getWindow().getAttributes().gravity =
                                    Gravity.BOTTOM;
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            return true;
                        }

                        case ExecutionThread.CONNECTION_ERROR: {
                            // Inform the error of the thread
                            showConnectionErrorDialog();
                            return true;
                        }
                    }
                    return false;
                }
            });
    private Button mStopAndRestartButton;
    private Button mFinishButton;
    private ExecutionThread mThread = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution);

        findViews();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        BlockFactory.setContext(getApplicationContext()); // Set context
        mSpaceManager.load();

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

        // Check this activity has already connected to the device
        Intent intent = getIntent();
        if (intent.getBooleanExtra("is_connected", false)) {
            setResult(Activity.RESULT_OK, intent);
            try {
                startExecution();
            } catch (RuntimeException e) {
                e.printStackTrace();
                showConnectionErrorDialog();
            }
        } else {
            if (hasBluetoothFunction()) {
                if (!mBtAdapter.isEnabled()) {
                    startActivityForResult(new Intent(BluetoothAdapter
                                                              .ACTION_REQUEST_ENABLE),
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

        // Check the thread is clearly dead
        if (mThread != null && mThread.isAlive()) {
            finishExecution();
        }
    }

    @Override
    public void onBackPressed() {
        if (mThread != null && mThread.isAlive()) {
            finishExecution();
        }
    }

    private void findViews() {
        mSpaceManager = new ProgressSpaceManager(this,
                                                 (BlockSpaceLayout)
                                                         findViewById(
                                                         R.id.execute_showingProgressLayout));
        mStopAndRestartButton =
                (Button) findViewById(R.id.execute_stopAndRestartButton);
        mFinishButton = (Button) findViewById(R.id.execute_finishButton);
    }

    private void stopExecution() {
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
        // check the tablet has bluetooth connecting
        if (mBtAdapter == null) {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(ExecutionActivityBase.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.execute_noBluetoothFunction)
                            .setPositiveButton(R.string.ok,
                                               new DialogInterface
                                                       .OnClickListener() {
                                                   @Override
                                                   public void onClick(
                                                           DialogInterface
                                                                   dialog,
                                                           int which) {
                                                       finish();
                                                   }
                                               })
                            .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            return false;
        }
        return true;
    }

    protected void showConnectionProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.execute_connecting);
        mProgressDialog.setMessage(getString(R.string.execute_pleaseWaitForAWhile));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    protected void dismissConnectionProgressDialog() {
        mProgressDialog.dismiss();
    }

    protected void showConnectionFailedDialog() {
        mConnectingHandler.sendEmptyMessage(FAILED_TO_CONNECT);
    }

    protected void startExecution() {
        if (mThread == null || !mThread.isAlive()) {
            mThread = new ExecutionThread(getApplicationContext(),
                                          mProgressHandler,
                                          getDeviceController());
            mThread.setPriority(Thread.MAX_PRIORITY);
            mThread.start();
        }
    }

    protected abstract MachineController getDeviceController();

    private void showConnectionErrorDialog() {
        AlertDialog alertDialog =
                new AlertDialog.Builder(ExecutionActivityBase.this).setTitle
                        (R.string.error)
                        .setMessage(R.string.execute_disconnectedByNXT)
                        .setPositiveButton(R.string.ok,
                                           new DialogInterface
                                                   .OnClickListener() {
                                               @Override
                                               public void onClick(
                                                       DialogInterface dialog,
                                                       int which) {
                                                   finish();
                                               }
                                           })
                        .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        // Inform this activity has been disconnected by the device
        Intent intent = new Intent();
        intent.putExtra("is_connected", false);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getBaseContext(),
                                   R.string.turnedOnBluetooth,
                                   Toast.LENGTH_SHORT).show();
                    connectToDevice();
                } else {
                    new AlertDialog.Builder(ExecutionActivityBase.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.bluetoothFunctionIsOff)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                }
                break;
        }
    }
}