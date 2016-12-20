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
package com.pileproject.drive.setting.machine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.pileproject.drive.R;
import com.pileproject.drive.machine.CarControllerBase;
import com.pileproject.drive.machine.Ev3CarController;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.view.PortTextViewBase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A fragment for setting the port connections. This fragment will be used by {@link Ev3PortConnectionFragment}.
 */
public class Ev3PortConnectionFragment extends DialogFragment {
    private View mRootView;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_portConnection);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_ev3_port_config, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // NOTE: this is necessary because this screen collapsed on API 23+
        resizeDialog();

        initializeSensorPorts();
        initializeMotorPorts();
    }

    private String loadSensor(int port, String sensor) {
        if (sensor.equals(CarControllerBase.InputDevice.NONE)) return sensor;

        PortTextViewBase portTextView = (PortTextViewBase) mRootView.findViewById(port);
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

        List<String> allSensors = Ev3CarController.SensorProperty.ALL_SENSORS;
        int index = 0;
        final int[] sensorPlaceIds = {
                R.id.setting_portconfig_sensor1, R.id.setting_portconfig_sensor2, R.id.setting_portconfig_sensor3,
        };
        for (String sensorType : allSensors) {
            if (sensorsInUsed.contains(sensorType)) continue;
            // set an unconnected sensor into a open space
            ((PortTextViewBase) mRootView.findViewById(sensorPlaceIds[index++])).setDeviceType(sensorType);
        }
    }

    private String loadMotor(int port, String motor) {
        if (motor.equals(CarControllerBase.OutputDevice.NONE)) return motor;

        PortTextViewBase portTextView = (PortTextViewBase) mRootView.findViewById(port);
        portTextView.setDeviceType(motor);
        return motor;
    }

    private void initializeMotorPorts() {
        MachinePreferences preferences = MachinePreferences.get(getActivity());

        Set<String> motorsInUsed = new HashSet<>();
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortA, preferences.getOutputPortA()));
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortB, preferences.getOutputPortB()));
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortC, preferences.getOutputPortC()));
        motorsInUsed.add(loadMotor(R.id.setting_portconfig_motorPortD, preferences.getOutputPortD()));

        List<String> allMotors = CarControllerBase.MotorProperty.ALL_MOTORS;
        int index = 0;
        final int[] motorPlaceIds = {
                R.id.setting_portconfig_motor1, R.id.setting_portconfig_motor2,
        };
        for (String motorType : allMotors) {
            if (motorsInUsed.contains(motorType)) continue;
            // set an unconnected motor into an open space
            ((PortTextViewBase) mRootView.findViewById(motorPlaceIds[index++])).setDeviceType(motorType);
        }
    }

    /**
     * This function should be called in {@link DialogFragment#onActivityCreated(Bundle)}.
     * Otherwise, the dialog size will never be changed
     */
    private void resizeDialog() {
        Dialog dialog = getDialog();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // resize window large enough to display list views
        int dialogWidth = (int) (metrics.widthPixels * 0.9);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
    }
}
