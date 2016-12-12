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
package com.pileproject.drive.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.MachinePreferences;

import static com.pileproject.drive.machine.CarControllerBase.OutputDevice.LEFT_MOTOR;
import static com.pileproject.drive.machine.CarControllerBase.OutputDevice.NONE;
import static com.pileproject.drive.machine.CarControllerBase.OutputDevice.RIGHT_MOTOR;

public class NxtMotorPortTextView extends PortTextViewBase {

    public NxtMotorPortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String getMotorName(Context context, String motorType) {
        if (LEFT_MOTOR.equals(motorType)) return context.getString(R.string.motors_left);
        if (RIGHT_MOTOR.equals(motorType)) return context.getString(R.string.motors_right);
        return "";
    }

    public static int getMotorColor(String motorType) {
        if (LEFT_MOTOR.equals(motorType)) return Color.rgb(70, 89, 183);
        if (RIGHT_MOTOR.equals(motorType)) return Color.rgb(214, 133, 52);
        return Color.GRAY;
    }

    @Override
    protected void savePortConnection(String port, String device) {
        final Context c = getContext();
        final MachinePreferences p = MachinePreferences.get(c);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortA).equals(port)) p.setOutputPortA(device);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortB).equals(port)) p.setOutputPortB(device);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortC).equals(port)) p.setOutputPortC(device);
    }

    @Override
    protected void removePortConnection(String port) {
        final Context c = getContext();
        final MachinePreferences p = MachinePreferences.get(c);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortA).equals(port)) p.setOutputPortA(NONE);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortB).equals(port)) p.setOutputPortB(NONE);
        if (c.getString(R.string.setting_portConnection_deviceMotorPortC).equals(port)) p.setOutputPortC(NONE);
    }

    @Override
    public String getDeviceType() {
        return mDeviceType;
    }

    @Override
    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
        this.setText(getMotorName(getContext(), deviceType));
        this.setBackgroundColor(getMotorColor(deviceType));
    }
}
