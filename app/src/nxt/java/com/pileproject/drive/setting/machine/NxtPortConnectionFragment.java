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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.util.SharedPreferencesWrapper;
import com.pileproject.drive.view.PortTextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class NxtPortConnectionFragment extends Fragment {
    private Activity mParentActivity;
    private View mRootView;

    public static void setDefaultValueOnPreferences(Context context) {
        Map<String, Integer> preferences = new HashMap<String, Integer>();

        preferences.put("sensorPort1", NxtController.SensorProperty.SENSOR_TOUCH);
        preferences.put("sensorPort2", NxtController.SensorProperty.SENSOR_SOUND);
        preferences.put("sensorPort3", NxtController.SensorProperty.SENSOR_LINE);
        preferences.put("motorPortB", NxtController.MotorProperty.MOTOR_LEFT);
        preferences.put("motorPortC", NxtController.MotorProperty.MOTOR_RIGHT);

        for (Entry<String, Integer> entry : preferences.entrySet()) {
            if (SharedPreferencesWrapper.loadIntPreference(context, entry.getKey(), -1) == -1) {
                SharedPreferencesWrapper.saveIntPreference(context, entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_nxtportconfig, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mParentActivity = getActivity();
        Context context = mParentActivity.getApplicationContext();

        setDefaultValueOnPreferences(context);
        initializeSensorPorts(context, mRootView);
        initializeMotorPorts(context, mRootView);
    }

    private void initializeSensorPorts(Context context, View root) {
        final int[] sensorPortIds = {R.id.setting_portconfig_sensorPort1, R.id.setting_portconfig_sensorPort2, R.id.setting_portconfig_sensorPort3, R.id.setting_portconfig_sensorPort4,};

        final int[] sensorPlaceIds = {R.id.setting_portconfig_sensor1, R.id.setting_portconfig_sensor2, R.id.setting_portconfig_sensor3,};

        LinkedList<Integer> sensorsInUsed = new LinkedList<Integer>();
        for (int i = 0; i < NxtController.SensorProperty.NUMBER_OF_SENSOR_PORTS; ++i) {
            PortTextView portTextView = (PortTextView) root.findViewById(sensorPortIds[i]);
            int sensorType = SharedPreferencesWrapper.loadIntPreference(context, portTextView.getPortName(), NxtController.SensorProperty.SENSOR_UNUSED);

            if (sensorType != NxtController.SensorProperty.SENSOR_UNUSED) {
                portTextView.setAttachmentType(sensorType);
                sensorsInUsed.add(sensorType);
            }
        }

        List<Integer> allSensors = NxtController.SensorProperty.getAllSensors();
        int index = 0;
        for (Integer sensorType : allSensors) {
            if (sensorsInUsed.indexOf(sensorType) == -1) {
                ((PortTextView) root.findViewById(sensorPlaceIds[index++])).setAttachmentType(sensorType);
            }
        }
    }

    private void initializeMotorPorts(Context context, View root) {
        final int[] motorPortIds = {R.id.setting_portconfig_motorPortA, R.id.setting_portconfig_motorPortB, R.id.setting_portconfig_motorPortC,};

        final int[] motorPlaceIds = {R.id.setting_portconfig_motor1, R.id.setting_portconfig_motor2,};

        LinkedList<Integer> motorsInUsed = new LinkedList<Integer>();
        for (int i = 0; i < NxtController.MotorProperty.NUMBER_OF_MOTOR_PORTS; ++i) {
            PortTextView portTextView = (PortTextView) root.findViewById(motorPortIds[i]);
            int motorType = SharedPreferencesWrapper.loadIntPreference(context, portTextView.getPortName(), NxtController.MotorProperty.MOTOR_UNUSED);

            if (motorType != NxtController.MotorProperty.MOTOR_UNUSED) {
                portTextView.setAttachmentType(motorType);
                motorsInUsed.add(motorType);
            }
        }

        List<Integer> allMotors = NxtController.MotorProperty.getAllMotors();
        int index = 0;
        for (Integer motorType : allMotors) {
            if (motorsInUsed.indexOf(motorType) == -1) {
                ((PortTextView) root.findViewById(motorPlaceIds[index++])).setAttachmentType(motorType);
            }
        }
    }
}
