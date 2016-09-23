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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.pileproject.drive.R;
import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.database.ProgramDataManager;
import com.pileproject.drive.programming.visual.block.BlockBase;

/**
 * A Thread class to execute program.
 */
public class ExecutionThread extends Thread {
    public static final int START_THREAD        = 1000;
    public static final int EMPHASIZE_BLOCK     = 1001;
    public static final int END_THREAD          = 1002;
    public static final int CONNECTION_ERROR    = 1003;
    private ProgramDataManager mManager;
    private ExecutionCondition mCondition;
    private MachineController mController;
    private Handler mUiHandler;
    private boolean mStop = false;
    private boolean mHalt = false;

    /**
     * Constructor
     *
     * @param uiHandler a handler for communicating the execution activity
     * @param controller a controller to control machines (e.g., NXT/EV3)
     */
    public ExecutionThread(Handler uiHandler, MachineController controller) {
        super();
        mManager = ProgramDataManager.getInstance();
        mController = controller;
        mUiHandler = uiHandler;
    }

    @Override
    public void run() {
        sendState(START_THREAD);

        mCondition = new ExecutionCondition(mManager.loadExecutionProgram());
        boolean isStopped = false;
        try {
            while (!mCondition.hasProgramFinished()) {

                // halt execution
                if (mHalt) {
                    break;
                }

                // stop temporarily
                if (mStop) {
                    if (!isStopped) {
                        mController.halt();
                        isStopped = !isStopped;
                    }
                    mCondition.decrementProgramCount(); // retry this iteration
                    continue;
                } else {
                    isStopped = false;
                }

                // check this block is to be through or not
                if (mCondition.isThrough()) {
                    continue;
                }

                // get block to be executed
                BlockBase block = mCondition.getCurrentBlock();

                // emphasize the current executing block
                sendIndex(EMPHASIZE_BLOCK, mCondition.getProgramCount());

                // do action and return delay
                int delay = block.action(mController, mCondition);
                waitMillSec(delay);
            }
            sendState(END_THREAD);

            // update the program count
            mCondition.incrementProgramCount();
        } catch (RuntimeException e) {
            sendState(CONNECTION_ERROR);
            e.printStackTrace();
        } finally {
            finalizeExecution();
        }
    }

    private void finalizeExecution() throws RuntimeException {
        mHalt = true;
        mStop = false;

        try {
            mController.finalize();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // wait in milliseconds
    private void waitMillSec(int milli) {
        try {
            Thread.sleep(milli); // sleep for milli sec
        } catch (InterruptedException e) {
            e.printStackTrace();
            // this often occurs while a program execution to stop it immediately
        }
    }

    /**
     * Stop this thread temporarily.
     */
    public void stopExecution() {
        mStop = true;
        notifyStatusChange();
    }

    /**
     * Restart this thread.
     */
    public void restartExecution() {
        mStop = false;
        notifyStatusChange();
    }

    /**
     * Halt this thread.
     */
    public void haltExecution() {
        mHalt = true;
        notifyStatusChange();
    }

    // interrupt
    private void notifyStatusChange() throws SecurityException {
        try {
            this.interrupt();
        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // create a bundle to send the change of state to activity
    private void sendState(int message) {
        Bundle bundle = new Bundle();
        Context context = DriveApplication.getContext();
        bundle.putInt(context.getString(R.string.key_execution_message), message);
        sendBundle(bundle);
    }

    // create a bundle to send the index of the current executing block to activity
    private void sendIndex(int message, int index) {
        Bundle bundle = new Bundle();
        Context context = DriveApplication.getContext();
        bundle.putInt(context.getString(R.string.key_execution_message), message);
        bundle.putInt(context.getString(R.string.key_execution_index), index);
        sendBundle(bundle);
    }

    // throw a bundle to activity to inform changes
    private void sendBundle(Bundle bundle) {
        Message message = Message.obtain();
        message.setData(bundle);
        mUiHandler.sendMessage(message);
    }
}
