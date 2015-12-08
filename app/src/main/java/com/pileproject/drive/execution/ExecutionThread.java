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
import android.util.Log;

import com.pileproject.drive.database.DBManager;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.selection.SelectionBlock;
import com.pileproject.drive.programming.visual.block.selection
        .SelectionEndBlock;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Sub thread to execute program
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class ExecutionThread extends Thread {
    public static final int START_THREAD = 1000;
    public static final int EMPHASIZE_BLOCK = 1001;
    public static final int END_THREAD = 1002;
    public static final int CONNECTION_ERROR = 1003;
    private static final String TAG = "ExecutionThread";
    private static int mThreadNum = 0;
    private boolean mHalt = false;
    private DBManager mManager;
    private ExecutionCondition mCondition;
    private MachineController mController;
    private Handler mUiHandler;
    private boolean mStop;

    /**
     * Constructor
     *
     * @param context   The context of Activity that calls this thread
     * @param uiHandler Handler for controlling the showing progress layout
     */
    public ExecutionThread(
            Context context, Handler uiHandler, MachineController controler) {
        super();
        mManager = new DBManager(context);
        mCondition = new ExecutionCondition();
        mController = controler;
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

        // Only one thread can be executed
        synchronized (this) {
            if (isExecutable()) {
                mThreadNum = 1;
            } else {
                return;
            }
        }

        mCondition.blocks = mManager.loadAll();
        boolean isStopped = false;
        try {
            for (mCondition.programCount = 0;
                 mCondition.programCount < mCondition.blocks.size();
                 mCondition.programCount++) {
                // Halt execution
                if (mHalt) {
                    break;
                }

                // Stop temporarily
                if (mStop) {
                    // Check already stopped
                    if (!isStopped) {
                        // Halt motors
                        mController.halt();
                        isStopped = !isStopped;
                    }
                    mCondition.programCount--; // Loop
                    continue;
                } else {
                    isStopped = false;
                }

                // Check ifStack is empty
                if (!mCondition.ifStack.isEmpty()) {
                    // Check this block is to be through
                    if (isThrough(mCondition.programCount)) {
                        continue;
                    }
                }

                // Get block to be executed
                BlockBase block =
                        mCondition.blocks.get(mCondition.programCount);
                Log.d(TAG,
                      "Current Block: " + block.getClass().getSimpleName());

                // Emphasize the current executing block
                sendIndex(EMPHASIZE_BLOCK, mCondition.programCount);

                // Do action and return delay
                int delay = block.action(mController, mCondition);
                waitMillSec(delay); // Wait for some

                Log.d(TAG, "Current Index: " + mCondition.programCount);

                if (mStop) {
                    mCondition.programCount--;
                }
                waitMillSec(1); // adjustment
            }
            Log.d(TAG, "Thread has just finished");
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

    // Wait on the millisecond time scale
    private void waitMillSec(int millsec) {
        try {
            Thread.sleep(millsec); // Sleep for milli sec
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check this block should be through
     * <p/>
     * TODO current version may not be applied to
     * nests of selection commands (NOT TESTED).
     *
     * @param index the index of the block that will be checked in this method
     * @return
     */
    private boolean isThrough(int index) {
        final int MARGIN = 80;
        ArrayList<BlockBase> blocks = mCondition.blocks;
        BlockBase block = blocks.get(index);

        if (block instanceof SelectionEndBlock) {
            return false;
        }

        HashMap<String, Integer> map = mCondition.ifStack.peek();

        // True
        if (map.get("result") == ExecutionCondition.TRUE) {
            BlockBase nearestSelectionBlock = blocks.get(map.get("index"));

            int max = nearestSelectionBlock.right;

            // Check the depth of nests
            int count = 1;
            for (int i = index; i < blocks.size(); i++) {
                BlockBase b = blocks.get(i);

                // Beginning of selection commands
                if (b.getKind() == SelectionBlock.class) {
                    count++;
                }
                // End of selection commands
                if (b.getKind() == SelectionEndBlock.class) {
                    count--;
                    // if count is not 0, this end block is the end of inner
                    // selection. Therefore, this block's right position may
                    // be the rightmost position.
                    if (count != 0 && max < b.right) {
                        max = b.right;
                        break;
                    }
                }

                if (count == 0) {
                    break;
                }
            }

            // If this block is right of max, return true
            if (max >= block.left + MARGIN) {
                return true;
            }
        }
        // False
        else if (map.get("result") == ExecutionCondition.FALSE) {
            BlockBase nearestSelectionBlock = blocks.get(map.get("index"));

            // Nest
            if (mCondition.ifStack.size() >= 2) {
                map = mCondition.ifStack.pop();
                // Get the index of the second nearest selection block
                int secondIndex = mCondition.ifStack.peek().get("index");
                BlockBase secondNearestSelectionBlock = blocks.get(secondIndex);
                mCondition.ifStack.push(map); // Push the popped element

                // Execute only if this block is under the nearest selection
                // block and the right side of the second nearest selection
                // block.
                if (secondNearestSelectionBlock.right >= block.left + MARGIN ||
                        nearestSelectionBlock.right < block.left + MARGIN) {
                    return true;
                }
            }
            // Not nest
            else {
                if (nearestSelectionBlock.right < block.left + MARGIN) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Stop this thread temporarily
     */
    public void stopExecution() {
        mStop = true;
        try {
            this.interrupt();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restart this thread
     */
    public void restartExecution() {
        mStop = false;
        try {
            this.interrupt();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Halt this thread
     */
    public void haltExecution() {
        mHalt = true;
        try {
            this.interrupt();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Create a bundle to send the change of state to Activity
    private void sendState(int message) {
        Bundle bundle = new Bundle();
        bundle.putInt("message", message);
        sendBundle(bundle);
    }

    // Create a bundle to send the index of the current executing block to
    // Activity
    private void sendIndex(int message, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt("message", message);
        bundle.putInt("index", index);
        sendBundle(bundle);
    }

    // Throw a bundle to Activity to inform changes
    private void sendBundle(Bundle bundle) {
        Message message = Message.obtain();
        message.setData(bundle);
        mUiHandler.sendMessage(message);
    }
}
