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

package com.pileproject.drive.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.preferences.MachinePreferencesSchema;

public class NxtMotorPortTextView extends PortTextView {

    public NxtMotorPortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String getMotorName(Context context, String motorType) {
        if (motorType.equals(MachinePreferencesSchema.MOTOR.LEFT))
            return context.getString(R.string.motors_left);
        if (motorType.equals(MachinePreferencesSchema.MOTOR.RIGHT))
            return context.getString(R.string.motors_right);
        return "";
    }

    public static int getMotorColor(String motorType) {
        if (motorType.equals(MachinePreferencesSchema.MOTOR.LEFT))
            return Color.rgb(70, 89, 183);
        if (motorType.equals(MachinePreferencesSchema.MOTOR.RIGHT))
            return Color.rgb(214, 133, 52);
        return Color.GRAY;
    }

    @Override
    protected void savePortConnection(String port, String device) {
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceMotorPortA)))
            MachinePreferences.get(getContext()).setOutputPortA(device);
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceMotorPortB)))
            MachinePreferences.get(getContext()).setOutputPortB(device);
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceMotorPortC)))
            MachinePreferences.get(getContext()).setOutputPortC(device);
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
