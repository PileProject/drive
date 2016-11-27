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

import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;

/**
 * A class that keeps the logic for visual block programs.
 */
public class BlockProgramLogic {
    /**
     * Check the current block will be executed or not.
     * The block can be ignored when it is in a if-statement.
     * @param condition
     * @return will be executed (true) or not (false)
     */
    static boolean willCurrentBlockBeExecuted(ExecutionCondition condition) {
        if (condition.sizeOfSelectionResult() == 0) return true;

        BlockBase block = condition.getCurrentBlock();
        if (block instanceof SelectionEndBlock) return true;

        ExecutionCondition.SelectionResult selectionResult = condition.peekSelectionResult();
        BlockBase nearestSelectionBlock = condition.getNearestSelectionBlock(selectionResult);

        int middleIf = (nearestSelectionBlock.getRight() + nearestSelectionBlock.getLeft()) / 2;
        int middle = (block.getRight() + block.getLeft()) / 2;

        // execute this block if (true && left) || (!false && right)
        return (selectionResult.result && middle <= middleIf) || (!selectionResult.result && middleIf < middle);
    }

}
