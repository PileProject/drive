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
package com.pileproject.drive.programming.visual.block.selection;

import android.content.Context;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.preferences.BlockPreferences;

/**
 * This block check the light sensor's value
 * If the value is higher than threshold, it means nxt is on the
 * light-colored floor and out of black line.
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class IfNxtIsOutOfLineBlock extends SelectionBlock {

    private int mThreshold;

    public IfNxtIsOutOfLineBlock(Context context) {
        super(context, R.layout.block_if_nxt_is_out_of_line);

        mThreshold = BlockPreferences.get(context).getLineSensorThreshold();
    }

    @Override
    protected boolean evaluateCondition(MachineController controller) {
        // comment is weird.
        // getLightPercent returns tenfold value
        return ((NxtController) controller).getLineSensorValue() > mThreshold * 10;
    }
}
