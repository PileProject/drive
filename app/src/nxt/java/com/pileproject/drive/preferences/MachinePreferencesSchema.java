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

import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

/**
 * Schema for "machine_preferences" table
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 21-April-2016
 */
@Table(name = "machine_preferences")
public class MachinePreferencesSchema {
    public class FIRMWARE {
        public static final String STANDARD = "standard";
        public static final String LEJOS = "lejos";
    }

    public class SENSOR {
        public static final String TOUCH = "touch_sensor";
        public static final String SOUND = "sound_sensor";
        public static final String LINE = "line_sensor";
        public static final String NONE = "none";
    }

    public class MOTOR {
        public static final String RIGHT = "right_motor";
        public static final String LEFT = "left_motor";
        public static final String NONE = "none";
    }

    @Key(name = "bluetooth_address")
    String macAddress;

    @Key(name = "firmware")
    final String firmware = FIRMWARE.STANDARD;

    @Key(name = "input_port_1")
    final String inputPort1 = SENSOR.TOUCH;

    @Key(name = "input_port_2")
    final String inputPort2 = SENSOR.SOUND;

    @Key(name = "input_port_3")
    final String inputPort3 = SENSOR.LINE;

    @Key(name = "input_port_4")
    final String inputPort4 = SENSOR.NONE;

    @Key(name = "output_port_A")
    final String outputPortA = MOTOR.NONE;

    @Key(name = "output_port_B")
    final String outputPortB = MOTOR.RIGHT;

    @Key(name = "output_port_C")
    final String outputPortC = MOTOR.LEFT;
}
