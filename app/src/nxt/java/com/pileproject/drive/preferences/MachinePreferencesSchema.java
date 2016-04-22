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
    @Key("bluetooth_address")
    String macAddress;

    @Key("firmware")
    String firmware = "standard";

    @Key("left_motor_port")
    int leftMotorPort = 0;

    @Key("right_motor_port")
    int rightMotorPort = 1;

    @Key("touch_sensor_port")
    int touchSensorPort = 0;

    @Key("line_sensor_port")
    int lineSensorPort = 1;

    @Key("sound_sensor_port")
    int soundSensorPort = 2;
}
