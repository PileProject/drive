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
 * Schema for "block_preferences" table
 */
@Table(name = "block_preferences")
public class BlockPreferencesSchema {
    public class THRESHOLD {
        public static final int SOUND_DEFAULT = 70;
        public static final int LINE_DEFAULT = 50;
        public static final int _DEFAULT = 50;
    }

    @Key(name = "sound_sensor_threshold")
    final int soundSensorThreshold = THRESHOLD.SOUND_DEFAULT;

    @Key(name = "line_sensor_threshold")
    final int lineSensorThreshold = THRESHOLD.LINE_DEFAULT;
}
