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
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;

/**
 * A class that provides helper methods for the logic of visual block programs.
 */
public class BlockProgramLogic {
    /**
     * Checks if the current block will be executed or not. The block will be executed when it is in an if-statement
     * and meets the condition. At present, the check is based on the position of blocks. A <p><b>selection block
     * section</b></p> is the area between selection blocks (the beginning and end). If a block is in the left side of
     * the section, the block will be executed when the condition of the if-statement is <code>true</code> and vice
     * versa.
     * In short, this method returns <code>true</code> when the current block in the selection block section AND
     * <ul>
     *      <li>in the left side of a selection block section AND the condition of the if-statement is
     *      <code>true</code></li>
     *      <li>in the right side of a selection block section AND the condition of the if-statement is
     *      <code>false</code></li>
     * </ul>
     * and returns <code>false</code> otherwise. The exceptions are blocks out of the selection block section and the
     * end block of a if-statement (they always should be executed).
     *
     * @param condition an {@link ExecutionCondition} instance which has the condition of a program
     * @return this block will be executed (<code>true</code>) or not (<code>false</code>)
     */
    static boolean willCurrentBlockBeExecuted(ExecutionCondition condition) {
        // no selection (if-statement) block
        if (condition.sizeOfSelectionResult() == 0) return true;

        BlockBase block = condition.getCurrentBlock();
        if (block instanceof SelectionEndBlock) return true; // should do the end of a selection operation

        ExecutionCondition.SelectionResult selectionResult = condition.peekSelectionResult();
        BlockBase nearestSelectionBlock = condition.getNearestSelectionBlock(selectionResult);

        int middleIf = (nearestSelectionBlock.getRight() + nearestSelectionBlock.getLeft()) / 2;
        int middle = (block.getRight() + block.getLeft()) / 2;

        // execute this block if (true && left) || (!false && right)
        return (selectionResult.result && middle <= middleIf) || (!selectionResult.result && middleIf < middle);
    }

}
