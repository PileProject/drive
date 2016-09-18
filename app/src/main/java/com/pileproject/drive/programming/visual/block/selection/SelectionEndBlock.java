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

package com.pileproject.drive.programming.visual.block.selection;

import android.content.Context;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.programming.visual.block.BlockBase;

/**
 * This block is the end of if commands
 *
 */
public class SelectionEndBlock extends BlockBase {

    public SelectionEndBlock(Context context) {
        super(context, R.layout.block_selection_end);
    }

    @Override
    public BlockKind getKind() {
        return BlockKind.SELECTION_END;
    }

    @Override
    public int action(MachineController controller, ExecutionCondition condition) {
        // just pop one
        condition.popSelectionResult();
        return 0;
    }
}