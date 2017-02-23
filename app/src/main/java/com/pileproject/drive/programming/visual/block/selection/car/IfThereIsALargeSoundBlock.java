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
package com.pileproject.drive.programming.visual.block.selection.car;

import android.content.Context;

import com.pileproject.drive.R;
import com.pileproject.drive.machine.CarControllerBase;
import com.pileproject.drive.machine.MachineController;
import com.pileproject.drive.preferences.BlockPreferences;
import com.pileproject.drive.programming.visual.block.selection.SelectionBlock;


/**
 * This block checks if the current sound sensor's value is larger than the threshold, which means there is a large
 * sound.
 */
public class IfThereIsALargeSoundBlock extends SelectionBlock {

    private int mThreshold;

    public IfThereIsALargeSoundBlock(Context context) {
        super(context, R.layout.block_if_there_is_a_large_sound);

        mThreshold = BlockPreferences.get(context).getSoundSensorThreshold();
    }

    @Override
    protected boolean evaluateCondition(MachineController controller) {
        return ((CarControllerBase) controller).getSoundSensorDb() > mThreshold;
    }
}
