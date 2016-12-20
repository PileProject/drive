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
package com.pileproject.drive.preferences;

import com.pileproject.drive.machine.CarControllerBase;
import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

/**
 * Schema for "machine_preferences" table
 */
@Table(name = "machine_preferences")
public class MachinePreferencesSchema {
    public static class Firmware {
        public static final String STANDARD = "standard";
        public static final String LEJOS = "lejos";
    }

    @Key(name = "bluetooth_address")
    String macAddress;

    @Key(name = "firmware")
    final String firmware = Firmware.STANDARD;

    @Key(name = "input_port_1")
    final String inputPort1 = CarControllerBase.InputDevice.TOUCH;

    @Key(name = "input_port_2")
    final String inputPort2 = CarControllerBase.InputDevice.SOUND;

    @Key(name = "input_port_3")
    final String inputPort3 = CarControllerBase.InputDevice.LINE;

    @Key(name = "input_port_4")
    final String inputPort4 = CarControllerBase.InputDevice.NONE;

    @Key(name = "output_port_A")
    final String outputPortA = CarControllerBase.OutputDevice.NONE;

    @Key(name = "output_port_B")
    final String outputPortB = CarControllerBase.OutputDevice.RIGHT_MOTOR;

    @Key(name = "output_port_C")
    final String outputPortC = CarControllerBase.OutputDevice.LEFT_MOTOR;
}
