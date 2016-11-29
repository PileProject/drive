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
import com.pileproject.drive.execution.CarControllerBase;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.preferences.MachinePreferencesSchema;

/**
 * This block checks the touch sensor was touched or not
 */
public class IfNxtWasTouchedBlock extends SelectionBlock {
    private final boolean mIsLejosFirmware;

    public IfNxtWasTouchedBlock(Context context) {
        super(context, R.layout.block_if_machine_was_touched);

        String firmware = MachinePreferences.get(getContext()).getFirmware();
        mIsLejosFirmware = firmware.equals(MachinePreferencesSchema.FIRMWARE.LEJOS);
    }

    @Override
    protected boolean evaluateCondition(CarControllerBase controller) {
        boolean wasTouched = ((NxtController) controller).isTouchSensorTouched();

        // leJOS returns the opposite value
        if (mIsLejosFirmware) wasTouched = !wasTouched;

        return wasTouched;
    }
}
