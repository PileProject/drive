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
package com.pileproject.drive.preferences;

import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

/**
 * A schema for "block_preferences" <code>SharedPreferences</code> table.
 *
 * @see <a href="https://github.com/rejasupotaro/kvs-schema">KVS Schema</a>
 */
@Table(name = "block_preferences")
public class BlockPreferencesSchema {
    @Key(name = "color_sensor_illuminance_threshold")
    int colorSensorIlluminanceThreshold;

    @Key(name = "gyro_sensor_rate_threshold")
    int gyroSensorRateThreshold;

    @Key(name = "gyro_sensor_angle_threshold")
    int gyroSensorAngleThreshold;

    @Key(name = "line_sensor_threshold")
    int lineSensorThreshold;

    @Key(name = "rangefinder_sensor_threshold")
    int rangefinderThreshold;

    @Key(name = "remote_control_receiver_distance_threshold")
    int remoteControlReceiverDistanceThreshold;

    @Key(name = "sound_sensor_threshold")
    int soundSensorThreshold;

    @Key(name = "touch_sensor_touched_count_threshold")
    int touchSensorTouchedCountThreshold;
}
