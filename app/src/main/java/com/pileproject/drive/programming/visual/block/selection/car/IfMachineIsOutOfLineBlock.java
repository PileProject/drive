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

import java.util.List;

/**
 * This block checks if the light sensor's value is higher than the threshold, which means the machine
 * is on a light-colored floor, thus out of a black line.
 */
public class IfMachineIsOutOfLineBlock extends SelectionBlock {

    private final double INVALID = -1_000_000;

    public IfMachineIsOutOfLineBlock(Context context) {
        super(context, R.layout.block_if_machine_is_out_of_line);
    }

    @Override
    protected boolean evaluateCondition(MachineController controller) {
        double threshold = INVALID;
        double realValue = INVALID;

        List<String> sensors =  ((CarControllerBase) controller).getAllInputDevices();
        if (sensors.contains(CarControllerBase.InputDevice.LIGHT)) {
            threshold = BlockPreferences.get(getContext()).getLightSensorThreshold();
            realValue = ((CarControllerBase) controller).getLightSensorValue();
        }
        if (sensors.contains(CarControllerBase.InputDevice.COLOR)) {
            threshold = BlockPreferences.get(getContext()).getColorSensorIlluminanceThreshold();
            realValue = ((CarControllerBase) controller).getColorSensorIlluminance();
        }

        if (threshold == INVALID || realValue == INVALID) {
            // TODO(tiwanari): throw an exception?
        }

        return realValue > threshold;
    }
}
