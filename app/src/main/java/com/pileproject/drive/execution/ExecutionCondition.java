/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
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

    public static class SelectionResult {
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
     * Checks if the program count is over than the program size or not.
     *
     * @return finished (<code>true</code>) or not (<code>false</code>)
     */
    public boolean hasProgramFinished() {
        return mProgramCount >= mBlocks.size();
    }

    /**
     * Gets the specified block with the specified selection result.
     *
     * @param result a {@link SelectionResult} instance that has the index of an if-block
     * @return a {@link BlockBase}
     * @throws IndexOutOfBoundsException if the {@link SelectionResult} has the index out of bounds
     */
    public BlockBase getNearestSelectionBlock(SelectionResult result) throws IndexOutOfBoundsException {
        return mBlocks.get(result.index);
    }

    /**
     * Gets the current block.
     *
     * @return the current {@link BlockBase}
     * @throws IndexOutOfBoundsException
     *      if the program already has been over (thus, the program count is out of bounds).
     */
    public BlockBase getCurrentBlock() throws IndexOutOfBoundsException {
        return mBlocks.get(mProgramCount);
    }

    /**
     * Increments the program count.
     */
    public void incrementProgramCount() {
        mProgramCount++;
    }

    /**
     * Decrements the program count.
     */
    public void decrementProgramCount() {
        mProgramCount--;
    }

    /**
     * Gets the current program count.
     *
     * @return the current program count
     */
    public int getProgramCount() {
        return mProgramCount;
    }

    /**
     * Pushes the pair of (the index of the selection block, the result: <code>true</code> or
     * <code>false</code>false) to the if-statements stack.
     *
     * @param result the result of a selection command (<code>true</code> or <code>false</code>)
     */
    public void pushSelectionResult(boolean result) {
        SelectionResult status = new SelectionResult(mProgramCount, result);
        mIfStack.push(status);
    }

    /**
     * Pops and throws away the latest selection result.
     *
     * @return a {@link SelectionResult}
     */
    public SelectionResult popSelectionResult() {
        return mIfStack.pop();
    }

    /**
     * Peeks the latest selection result.
     *
     * @return a {@link SelectionResult}
     */
    public SelectionResult peekSelectionResult() {
        return mIfStack.peek();
    }

    /**
     * Gets the size of selection results.
     *
     * @return the size
     */
    public int sizeOfSelectionResult() {
        return mIfStack.size();
    }

    /**
     * Pushes the index of the beginning block of the current loop to a loop-statement stack. This will be used by
     * {@link com.pileproject.drive.programming.visual.block.repetition.LoopBlock} or
     * {@link com.pileproject.drive.programming.visual.block.repetition.NTimesBlock}.
     *
     * @param index the index of the beginning loop block
     */
    private void pushBeginningOfLoop(int index) {
        mWhileStack.push(index);
        mBeginningOfCurrentLoop = index >= 0 ?
                index : index - FOREVER_WHILE_OFFSET;
    }

    /**
     * Enters an infinite loop.
     */
    public void enterInfiniteLoop() {
        int index = getProgramCount();
        pushBeginningOfLoop(index + FOREVER_WHILE_OFFSET); // push with offset
    }

    /**
     * Enters an N-times loop.
     *
     * @param count the number of times this loop should be repeated
     */
    public void enterNTimesLoop(int count) {
        int index = getProgramCount();

        for (int i = 1; i < count; i++)
            pushBeginningOfLoop(index);
    }

    /**
     * Reaches the end of loop. This method deals with the finalizing process of loops.
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
     * Breaks out the current loop.
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
