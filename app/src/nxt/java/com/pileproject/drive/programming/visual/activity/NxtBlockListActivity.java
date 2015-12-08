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

package com.pileproject.drive.programming.visual.activity;

import com.pileproject.drive.programming.visual.block.repetition
        .RepetitionBreakBlock;
import com.pileproject.drive.programming.visual.block.repetition
        .WhileForeverBlock;
import com.pileproject.drive.programming.visual.block.repetition.WhileNumBlock;
import com.pileproject.drive.programming.visual.block.selection
        .IfNXTIsOutOfLineBlock;
import com.pileproject.drive.programming.visual.block.selection
        .IfNXTWasTouchedBlock;
import com.pileproject.drive.programming.visual.block.selection
        .IfThereWasALargeSoundBlock;
import com.pileproject.drive.programming.visual.block.sequence.BackwardSecBlock;
import com.pileproject.drive.programming.visual.block.sequence.ForwardSecBlock;
import com.pileproject.drive.programming.visual.block.sequence
        .SetLeftMotorSpeedBlock;
import com.pileproject.drive.programming.visual.block.sequence
        .SetRightMotorSpeedBlock;
import com.pileproject.drive.programming.visual.block.sequence.StopSecBlock;
import com.pileproject.drive.programming.visual.block.sequence.TurnLeftSecBlock;
import com.pileproject.drive.programming.visual.block.sequence
        .TurnRightSecBlock;

public class NxtBlockListActivity extends BlockListActivityBase {
    @Override
    protected BlockClassHolder[][] getBlockIcons() {
        BlockClassHolder[][] blocks = {
                {
                        new BlockClassHolder(ForwardSecBlock.class),
                        new BlockClassHolder(BackwardSecBlock.class),
                        new BlockClassHolder(TurnRightSecBlock.class),
                        new BlockClassHolder(TurnLeftSecBlock.class),
                        new BlockClassHolder(StopSecBlock.class),
                        new BlockClassHolder(SetLeftMotorSpeedBlock.class),
                        new BlockClassHolder(SetRightMotorSpeedBlock.class),
                }, {
                        new BlockClassHolder(WhileForeverBlock.class),
                        new BlockClassHolder(WhileNumBlock.class),
                        new BlockClassHolder(RepetitionBreakBlock.class),
                }, {
                        new BlockClassHolder(IfNXTIsOutOfLineBlock.class),
                        new BlockClassHolder(IfNXTWasTouchedBlock.class),
                        new BlockClassHolder(IfThereWasALargeSoundBlock.class),
                },
        };
        return blocks;
    }
}
