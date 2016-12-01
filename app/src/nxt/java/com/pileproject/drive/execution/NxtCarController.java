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

import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.preferences.MachinePreferencesSchema;
import com.pileproject.drivecommand.model.nxt.NxtMachine;
import com.pileproject.drivecommand.model.nxt.port.NxtInputPort;
import com.pileproject.drivecommand.model.nxt.port.NxtOutputPort;

import java.util.LinkedList;
import java.util.List;

import static com.pileproject.drive.app.DriveApplication.getContext;
import static com.pileproject.drive.execution.CarControllerBase.INPUT.LINE;
import static com.pileproject.drive.execution.CarControllerBase.INPUT.SOUND;
import static com.pileproject.drive.execution.CarControllerBase.INPUT.TOUCH;

public class NxtCarController extends CarControllerBase {
    private final boolean mIsLejosFirmware;

    /**
     * binds each sensor and motor to their port
     */
    public NxtCarController(NxtMachine machine) {
        mMachine = machine;

        MachinePreferences preferences = MachinePreferences.get(getContext());

        // connect motors
        connectOutputPort(preferences.getOutputPortA(), NxtOutputPort.PORT_A);
        connectOutputPort(preferences.getOutputPortB(), NxtOutputPort.PORT_B);
        connectOutputPort(preferences.getOutputPortC(), NxtOutputPort.PORT_C);

        // connect sensors
        connectInputPort(preferences.getInputPort1(), NxtInputPort.PORT_1);
        connectInputPort(preferences.getInputPort2(), NxtInputPort.PORT_2);
        connectInputPort(preferences.getInputPort3(), NxtInputPort.PORT_3);
        connectInputPort(preferences.getInputPort4(), NxtInputPort.PORT_4);

        String firmware = MachinePreferences.get(getContext()).getFirmware();
        mIsLejosFirmware = firmware.equals(MachinePreferencesSchema.FIRMWARE.LEJOS);
    }

    /**
     * Checks the touch sensor is touched or not now.
     *
     * @return is touched (true) or not (false)
     */
    public boolean isTouchSensorTouched() {
        if (mTouchSensor == null) return false;
        boolean isTouched = mTouchSensor.isTouched();

        // leJOS returns the opposite value
        if (mIsLejosFirmware) isTouched = !isTouched;

        return isTouched;
    }

    /**
     * Internal class to contain sensor properties
     * particularly for Android activities
     */
    public static final class SensorProperty {
        public static List<String> getAllSensors() {
            List<String> sensors = new LinkedList<>();
            sensors.add(TOUCH);
            sensors.add(SOUND);
            sensors.add(LINE);
            return sensors;
        }

        public static final class LineSensor {
            public static final int PctMin = 0;
            public static final int PctMax = 100;
        }

        public static final class SoundSensor {
            public static final int SI_dB_SiMin = 40;
            public static final int SI_dB_SiMax = 120;
        }
    }
}