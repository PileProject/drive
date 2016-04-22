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


public class NxtSensorPortTextView extends PortTextView {

    public NxtSensorPortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String getSensorName(Context context, String sensorType) {
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.TOUCH))
            return context.getString(R.string.sensors_touch);
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.SOUND))
            return context.getString(R.string.sensors_sound);
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.LINE))
            return context.getString(R.string.sensors_light);
        return "";
    }

    public static int getSensorColor(String sensorType) {
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.TOUCH))
            return Color.rgb(50, 142, 183);
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.SOUND))
            return Color.rgb(65, 163, 86);
        if (sensorType.equals(MachinePreferencesSchema.SENSOR.LINE))
            return Color.rgb(221, 86, 82);
        return Color.GRAY;
    }

    @Override
    protected void savePortConnection(String port, String device) {
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceSensorPort1)))
            MachinePreferences.get(getContext()).setInputPort1(device);
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceSensorPort2)))
            MachinePreferences.get(getContext()).setInputPort2(device);
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceSensorPort3)))
            MachinePreferences.get(getContext()).setInputPort3(device);
        if (port.equals(getContext().getString(R.string.setting_portconfig_deviceSensorPort4)))
            MachinePreferences.get(getContext()).setInputPort4(device);
    }

    @Override
    public String getDeviceType() {
        return mDeviceType;
    }

    @Override
    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
        this.setText(getSensorName(getContext(), deviceType));
        this.setBackgroundColor(getSensorColor(deviceType));
    }
}
