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
package com.pileproject.drive.machine;

import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drivecommand.model.ev3.Ev3Machine;
import com.pileproject.drivecommand.model.ev3.port.Ev3InputPort;
import com.pileproject.drivecommand.model.ev3.port.Ev3OutputPort;

import java.util.Arrays;
import java.util.List;

import static com.pileproject.drive.app.DriveApplication.getContext;
import static com.pileproject.drive.machine.CarControllerBase.InputDevice.COLOR;
import static com.pileproject.drive.machine.CarControllerBase.InputDevice.RANGEFINDER;
import static com.pileproject.drive.machine.CarControllerBase.InputDevice.TOUCH;

/**
 * An implementation of {@link CarControllerBase} that controls LEGO MINDSTORMS EV3.
 */
public class Ev3CarController extends CarControllerBase {
    /**
     * An internal class that contains sensor properties.
     */
    public static final class SensorProperty {
        public static final List<String> ALL_SENSORS = Arrays.asList(
                        TOUCH,
                        COLOR,
                        RANGEFINDER
                );

        public static final class ColorSensor {
            public static final int PCT_MIN = 0;
            public static final int PCT_MAX = 100;
        }

        public static final class Rangefinder {
            public static final int CM_MIN = 0;
            public static final int CM_MAX = 255;
        }
    }

    /**
     * Binds each sensor and motor to their ports by using <code>MachinePreferences</code> which is based on
     * {@link com.pileproject.drive.preferences.MachinePreferencesSchema}.
     *
     * @param machine an {@link Ev3Machine} to be manipulated
     */
    public Ev3CarController(Ev3Machine machine) {
        mMachine = machine;

        MachinePreferences preferences = MachinePreferences.get(getContext());

        // connect motors
        connectOutputPort(preferences.getOutputPortA(), Ev3OutputPort.PORT_A);
        connectOutputPort(preferences.getOutputPortB(), Ev3OutputPort.PORT_B);
        connectOutputPort(preferences.getOutputPortC(), Ev3OutputPort.PORT_C);
        connectOutputPort(preferences.getOutputPortD(), Ev3OutputPort.PORT_D);

        // connect sensors
        connectInputPort(preferences.getInputPort1(), Ev3InputPort.PORT_1);
        connectInputPort(preferences.getInputPort2(), Ev3InputPort.PORT_2);
        connectInputPort(preferences.getInputPort3(), Ev3InputPort.PORT_3);
        connectInputPort(preferences.getInputPort4(), Ev3InputPort.PORT_4);
    }

    @Override
    public List<String> getAllInputDevices() {
        return SensorProperty.ALL_SENSORS;
    }

    @Override
    public int getColorSensorIlluminance() {
        if (mColorSensor == null) return -1;
        return mColorSensor.getIlluminance();
    }

    @Override
    public int getRangefinderDistance() {
        if (mRangefinder == null) return -1;
        return mRangefinder.getDistance();
    }

    @Override
    public boolean isTouchSensorTouched() {
        if (mTouchSensor == null) return false;
        return mTouchSensor.isTouched();
    }

    @Override
    public List<String> getAllOutputDevices() {
        // NOTE: add more devices if we want to use them (e.g., Buzzer)
        return CarControllerBase.MotorProperty.ALL_MOTORS;
    }

}
