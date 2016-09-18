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
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.preferences.BlockPreferences;


/**
 * This block check the sound sensor's value and check there
 * was a large sound or not
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class IfThereWasALargeSoundBlock extends SelectionBlock {

    private int mThreshold;

    public IfThereWasALargeSoundBlock(Context context) {
        super(context, R.layout.block_if_there_was_a_large_sound);

        mThreshold = BlockPreferences.get(context).getSoundSensorThreshold();
    }

    @Override
    protected boolean evaluateCondition(MachineController controller) {
        // need multiply 10 because getdB returns 10 times value
        // the comment is messed up
        return ((NxtController) controller).getSoundSensorValue() > mThreshold * 10;
    }
}
