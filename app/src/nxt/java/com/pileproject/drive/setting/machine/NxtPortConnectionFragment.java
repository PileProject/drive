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

package com.pileproject.drive.setting.machine;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.preferences.MachinePreferencesSchema;
import com.pileproject.drive.view.PortTextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NxtPortConnectionFragment extends PreferenceFragment {
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_nxtportconfig, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSensorPorts();
        initializeMotorPorts();
    }

    private String loadSensor(int port, String sensor) {
        if (sensor.equals(MachinePreferencesSchema.SENSOR.NONE)) return sensor;

        PortTextView portTextView = (PortTextView) mRootView.findViewById(port);
        portTextView.setDeviceType(sensor);
        return sensor;
    }

    private void initializeSensorPorts() {
        MachinePreferences preferences = MachinePreferences.get(getActivity());

        Set<String> sensorsInUsed = new HashSet<>();
        sensorsInUsed.add(loadSensor(R.id.setting_portconfig_sensorPort1, preferences.getInputPort1()));
        sensorsInUsed.add(loadSensor(R.id.setting_portconfig_sensorPort2, preferences.getInputPort2()));
        sensorsInUsed.add(loadSensor(R.id.setting_portconfig_sensorPort3, preferences.getInputPort3()));
        sensorsInUsed.add(loadSensor(R.id.setting_portconfig_sensorPort4, preferences.getInputPort4()));

        List<String> allSensors = NxtController.SensorProperty.getAllSensors();
        int index = 0;
        final int[] sensorPlaceIds = {
                R.id.setting_portconfig_sensor1, R.id.setting_portconfig_sensor2, R.id.setting_portconfig_sensor3,
        };
        for (String sensorType : allSensors) {
            if (sensorsInUsed.contains(sensorType)) continue;
            // set an unconnected sensor into a open space
            ((PortTextView) mRootView.findViewById(sensorPlaceIds[index++])).setDeviceType(sensorType);
        }
    }

    private String loadMotor(int port, String motor) {
        if (motor.equals(MachinePreferencesSchema.MOTOR.NONE)) return motor;

        PortTextView portTextView = (PortTextView) mRootView.findViewById(port);
        portTextView.setDeviceType(motor);
        return motor;
    }

    private void initializeMotorPorts() {
        MachinePreferences preferences = MachinePreferences.get(getActivity());

        Set<String> motorsInUsed = new HashSet<>();
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortA, preferences.getOutputPortA()));
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortB, preferences.getOutputPortB()));
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortC, preferences.getOutputPortC()));

        List<String> allMotors = NxtController.MotorProperty.getAllMotors();
        int index = 0;
        final int[] motorPlaceIds = {
                R.id.setting_portconfig_motor1, R.id.setting_portconfig_motor2,
        };
        for (String motorType : allMotors) {
            if (motorsInUsed.contains(motorType)) continue;;
            // set an unconnected motor into an open space
            ((PortTextView) mRootView.findViewById(motorPlaceIds[index++])).setDeviceType(motorType);
        }
    }
}
