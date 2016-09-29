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

import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.Stack;

/**
 * A container class that has the condition of program execution.
 */
public class ExecutionCondition {
    private static final int FOREVER_WHILE_OFFSET = -1000;

    private Stack<Integer> mWhileStack;
    private Stack<SelectionResult> mIfStack;
    private int mBeginningOfCurrentLoop;
    private int mProgramCount;
    private final List<BlockBase> mBlocks;

    public class SelectionResult {
        public final int index;
        public final boolean result;

        public SelectionResult(int index, boolean result) {
            this.index = index;
            this.result = result;
        }
    }

    public ExecutionCondition(List<BlockBase> blocks) {
        // convert a list with random access support
        if (!(blocks instanceof RandomAccess)) blocks = new ArrayList<>(blocks);

        mBlocks = blocks;
        Collections.unmodifiableList(mBlocks);

        mWhileStack = new Stack<>();
        mIfStack = new Stack<>();

        mBeginningOfCurrentLoop = -1;
        mProgramCount = 0;
    }

    /**
     * Check the program count is over than the program size or not.
     * @return finished (true) or not (false)
     */
    public boolean hasProgramFinished() {
        return mProgramCount >= mBlocks.size();
    }

    /**
     * Get the specified block with a selection result (it has index as a member).
     * @param result SelectionResult instance that has the index of an if-block
     * @return
     * @throws IndexOutOfBoundsException
     */
    public BlockBase getNearestSelectionBlock(SelectionResult result) throws IndexOutOfBoundsException {
        return mBlocks.get(result.index);
    }

    /**
     * Get the current block.
     * @return
     * @throws IndexOutOfBoundsException
     */
    public BlockBase getCurrentBlock() throws IndexOutOfBoundsException {
        return mBlocks.get(mProgramCount);
    }

    /**
     * Increment the program count.
     */
    public void incrementProgramCount() {
        mProgramCount++;
    }

    /**
     * Decrement the program count.
     */
    public void decrementProgramCount() {
        mProgramCount--;
    }

    /**
     * Get the current program count.
     * @return program count
     */
    public int getProgramCount() {
        return mProgramCount;
    }

    /**
     * Push the pair of (the index of the selection block, the result: true or false) to ifStack
     * @param result the result of a selection command
     */
    public void pushSelectionResult(boolean result) {
        SelectionResult status = new SelectionResult(mProgramCount, result);
        mIfStack.push(status);
    }

    /**
     * Pop and throw away the latest selection result.
     * @return
     */
    public SelectionResult popSelectionResult() {
        return mIfStack.pop();
    }

    /**
     * Peek the latest selection result.
     * @return
     */
    public SelectionResult peekSelectionResult() {
        return mIfStack.peek();
    }

    /**
     * Get the size of selection results.
     * @return
     */
    public int sizeOfSelectionResult() {
        return mIfStack.size();
    }

    /**
     * Push the index of the beginning block of the current loop to a stack.
     * @param index the index of the beginning block
     */
    private void pushBeginningOfLoop(int index) {
        mWhileStack.push(index);
        mBeginningOfCurrentLoop = index >= 0 ?
                index : index - FOREVER_WHILE_OFFSET;
    }

    /**
     * Enters infinite loop.
     */
    public void enterInfiniteLoop() {
        int index = getProgramCount();
        pushBeginningOfLoop(index + FOREVER_WHILE_OFFSET); // push with offset
    }

    /**
     * Enters N-times loop.
     * @param count number of times this loop repeats.
     */
    public void enterNTimesLoop(int count) {
        int index = getProgramCount();
        for (int i = 1; i < count; i++) {
            pushBeginningOfLoop(index);
        }
    }

    /**
     * Reach the end of loop.
     */
    public void reachEndOfLoop() {
        if (mWhileStack.isEmpty()) return ;

        int index = mWhileStack.peek() >= 0 ?
                    mWhileStack.peek() : mWhileStack.peek() - FOREVER_WHILE_OFFSET;

        // the loop has already finished
        if (mBeginningOfCurrentLoop != index) {
            mBeginningOfCurrentLoop = mWhileStack.peek();
        }
        // the loop has not finished yet
        else {
            // go back to the beginning of the current loop
           mProgramCount = index;
        }

        // if the loop is not 'forever while', pop one
        if (mWhileStack.peek() >= 0) mProgramCount = mWhileStack.pop();
    }

    /**
     * Break the current loop.
     */
    public void breakLoop() {
        if (mWhileStack.isEmpty()) return;

        // remove the indices of current while loop
        int index = mWhileStack.peek(); // target index
        while (!mWhileStack.isEmpty() && index == mWhileStack.peek()) mWhileStack.pop();

        // update index
        if (!mWhileStack.isEmpty()) {
            mBeginningOfCurrentLoop = mWhileStack.peek() >= 0 ?
                mWhileStack.peek() : mWhileStack.peek() - FOREVER_WHILE_OFFSET;
        } else {
            mBeginningOfCurrentLoop = -1;
        }

        // remove selection commands that this loop contains
        while (!mIfStack.isEmpty() && mIfStack.peek().index >= index) mIfStack.pop();

        // move to the end of the current loop
        for (; mBlocks.size() >= mProgramCount; ++mProgramCount) {
            if (mBlocks.get(mProgramCount).getKind() == BlockBase.BlockKind.REPETITION_END) break;
        }
    }
}
