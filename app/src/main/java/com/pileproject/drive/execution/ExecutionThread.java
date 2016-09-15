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
import com.pileproject.drive.programming.visual.block.selection.SelectionBlock;
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;

import java.util.ArrayList;

/**
 * A Thread class to execute program.
 */
public class ExecutionThread extends Thread {
    public static final int START_THREAD = 1000;
    public static final int EMPHASIZE_BLOCK = 1001;
    public static final int END_THREAD = 1002;
    public static final int CONNECTION_ERROR = 1003;
    private static int mThreadNum = 0;
    private boolean mHalt = false;
    private ProgramDataManager mManager;
    private ExecutionCondition mCondition;
    private MachineController mController;
    private Handler mUiHandler;
    private boolean mStop;

    /**
     * Constructor
     *
     * @param uiHandler a handler for communicating the execution activity
     * @param controller a controller to control machines (e.g., NXT/EV3)
     */
    public ExecutionThread(Handler uiHandler, MachineController controller) {
        super();
        mManager = ProgramDataManager.getInstance();
        mCondition = new ExecutionCondition();
        mController = controller;
        mUiHandler = uiHandler;
    }

    private boolean isExecutable() {
        synchronized (this) {
            return (mThreadNum == 0);
        }
    }

    @Override
    public void run() {
        sendState(START_THREAD);

        // just one thread can be executed
        synchronized (this) {
            if (isExecutable()) {
                mThreadNum = 1;
            } else {
                return;
            }
        }

        mCondition.blocks = mManager.loadExecutionProgram();
        boolean isStopped = false;
        try {
            for (mCondition.programCount = 0;
                 mCondition.programCount < mCondition.blocks.size();
                 mCondition.programCount++) {
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
                    mCondition.programCount--; // loop
                    continue;
                } else {
                    isStopped = false;
                }

                // check whether ifStack is empty
                if (!mCondition.ifStack.isEmpty()) {
                    // check this block is to be through
                    if (isThrough(mCondition.programCount)) {
                        continue;
                    }
                }

                // get block to be executed
                BlockBase block = mCondition.blocks.get(mCondition.programCount);

                // emphasize the current executing block
                sendIndex(EMPHASIZE_BLOCK, mCondition.programCount);

                // do action and return delay
                int delay = block.action(mController, mCondition);
                waitMillSec(delay);

                if (mStop) {
                    mCondition.programCount--;
                }
                waitMillSec(1); // adjustment
            }
            sendState(END_THREAD);
        } catch (RuntimeException e) {
            sendState(CONNECTION_ERROR);
            e.printStackTrace();
        } finally {
            finalizeExecution();
        }
    }

    private void finalizeExecution() {
        mHalt = true;
        mStop = false;
        mThreadNum = 0;

        try {
            mController.finalize();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    // wait in milliseconds
    private void waitMillSec(int milli) {
        try {
            Thread.sleep(milli); // sleep for milli sec
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check this block should be through
     * NOTE: current version may not be applied to
     * nests of selection commands (NOT TESTED).
     *
     * @param index the index of the block that will be checked in this method
     * @return
     */
    private boolean isThrough(int index) {
        final int MARGIN = 80;
        ArrayList<BlockBase> blocks = mCondition.blocks;
        BlockBase block = blocks.get(index);
        Context context = DriveApplication.getContext();

        if (block instanceof SelectionEndBlock) {
            return false;
        }

        ExecutionCondition.IfStatus ifStatus = mCondition.ifStack.peek();

        // true
        if (ifStatus.result) {
            BlockBase nearestSelectionBlock = blocks.get(ifStatus.index);

            int max = nearestSelectionBlock.getLeft() + nearestSelectionBlock.getMeasuredWidth();

            // check the depth of nests
            int count = 1;
            for (int i = index; i < blocks.size(); i++) {
                BlockBase b = blocks.get(i);

                // beginning of selection commands
                if (b.getKind() == SelectionBlock.class) {
                    count++;
                }
                // end of selection commands
                if (b.getKind() == SelectionEndBlock.class) {
                    count--;
                    // if count is not 0, this end block is the end of inner
                    // selection. Therefore, this block's right position may
                    // be the rightmost position.
                    if (count != 0 && max < b.getLeft() + b.getMeasuredWidth()) {
                        max = b.getLeft() + b.getMeasuredWidth();
                        break;
                    }
                }

                if (count == 0) {
                    break;
                }
            }

            // if this block is right of max, return true
            if (max >= block.getLeft() + MARGIN) {
                return true;
            }
        }
        // false
        else if (ifStatus.result) {
            BlockBase nearestSelectionBlock = blocks.get(ifStatus.index);

            // nest
            if (mCondition.ifStack.size() >= 2) {
                ifStatus = mCondition.ifStack.pop();
                // get the index of the second nearest selection block
                int secondIndex
                    = mCondition.ifStack.peek().index;
                BlockBase secondNearestSelectionBlock = blocks.get(secondIndex);
                mCondition.ifStack.push(ifStatus); // push the popped element

                // execute this block only if it is under the nearest selection block
                // and the right side of the second nearest selection block
                if ((secondNearestSelectionBlock.getLeft() + secondNearestSelectionBlock.getMeasuredWidth() >=
                        block.getLeft() + MARGIN) ||
                        (nearestSelectionBlock.getLeft() + nearestSelectionBlock.getMeasuredWidth() <
                                block.getLeft() + MARGIN)) {
                    return true;
                }
            }
            // not nest
            else {
                if (nearestSelectionBlock.getLeft() + nearestSelectionBlock.getMeasuredWidth() <
                        block.getLeft() + MARGIN) {
                    return true;
                }
            }
        }

        return false;
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

    private void notifyStatusChange() {
        try {
            this.interrupt();
        } catch (SecurityException e) {
            e.printStackTrace();
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
