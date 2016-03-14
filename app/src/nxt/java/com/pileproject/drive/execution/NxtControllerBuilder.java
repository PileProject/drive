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

package com.pileproject.drive.execution;

import android.content.Context;
import android.util.SparseArray;

import com.pileproject.drive.util.SharedPreferencesWrapper;
import com.pileproject.drivecommand.model.nxt.port.NxtInputPort;
import com.pileproject.drivecommand.model.nxt.port.NxtOutputPort;

public class NxtControllerBuilder {
    private SparseArray<NxtInputPort> mSensorPorts = null;
    private SparseArray<NxtOutputPort> mMotorPorts = null;

    public NxtControllerBuilder(Context context) {
        mSensorPorts = new SparseArray<>();
        mMotorPorts = new SparseArray<>();

        loadSensorsFromPreferences(context);
        loadMotorsFromPreferences(context);
    }

    public NxtInputPort getSensorPort(int sensorType) {
        return mSensorPorts.get(sensorType);
    }

    public NxtOutputPort getMotorPort(int motorType) {
        return mMotorPorts.get(motorType);
    }

    private void loadSensorsFromPreferences(Context context) {
        final int notAssigned = NxtController.SensorProperty.SENSOR_UNUSED;

        final NxtInputPort[] sensorPorts = {
                NxtInputPort.PORT_1, NxtInputPort.PORT_2, NxtInputPort.PORT_3, NxtInputPort.PORT_4
        };
        final String[] sensorPortPrefTags = {
                NxtController.TAG_SENSOR_PORT_1,
                NxtController.TAG_SENSOR_PORT_2,
                NxtController.TAG_SENSOR_PORT_3,
                NxtController.TAG_SENSOR_PORT_4
        };

        for (int i = 0; i < sensorPortPrefTags.length; ++i) {
            int key =
                    SharedPreferencesWrapper
                            .loadIntPreference(sensorPortPrefTags[i], notAssigned);
            mSensorPorts.put(key, sensorPorts[i]);
        }
    }

    private void loadMotorsFromPreferences(Context context) {
        final int notAssigned = NxtController.MotorProperty.MOTOR_UNUSED;

        final NxtOutputPort[] motorPorts = {
                NxtOutputPort.PORT_A, NxtOutputPort.PORT_B, NxtOutputPort.PORT_C
        };
        final String[] motorPortPrefTags = {
                NxtController.TAG_MOTOR_PORT_A,
                NxtController.TAG_MOTOR_PORT_B,
                NxtController.TAG_MOTOR_PORT_C
        };

        for (int i = 0; i < motorPortPrefTags.length; ++i) {
            int key =
                    SharedPreferencesWrapper
                            .loadIntPreference(motorPortPrefTags[i], notAssigned);
            mMotorPorts.put(key, motorPorts[i]);
        }
    }
}
