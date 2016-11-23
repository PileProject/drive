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

import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.NONE;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.TOUCH;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.SOUND;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.LINE;

public class NxtSensorPortTextView extends PortTextViewBase {

    public NxtSensorPortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String getSensorName(Context context, String sensorType) {
        if (TOUCH.equals(sensorType)) return context.getString(R.string.sensors_touch);
        if (SOUND.equals(sensorType)) return context.getString(R.string.sensors_sound);
        if (LINE.equals(sensorType)) return context.getString(R.string.sensors_light);
        return "";
    }

    public static int getSensorColor(String sensorType) {
        if (TOUCH.equals(sensorType)) return Color.rgb(50, 142, 183);
        if (SOUND.equals(sensorType)) return Color.rgb(65, 163, 86);
        if (LINE.equals(sensorType)) return Color.rgb(221, 86, 82);
        return Color.GRAY;
    }

    @Override
    protected void savePortConnection(String port, String device) {
        final Context c = getContext();
        final MachinePreferences p = MachinePreferences.get(c);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort1).equals(port)) p.setInputPort1(device);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort2).equals(port)) p.setInputPort2(device);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort3).equals(port)) p.setInputPort3(device);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort4).equals(port)) p.setInputPort4(device);
    }

    @Override
    protected void removePortConnection(String port) {
        final Context c = getContext();
        final MachinePreferences p = MachinePreferences.get(c);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort1).equals(port)) p.setInputPort1(NONE);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort2).equals(port)) p.setInputPort2(NONE);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort3).equals(port)) p.setInputPort3(NONE);
        if (c.getString(R.string.setting_portConnection_deviceSensorPort4).equals(port)) p.setInputPort4(NONE);
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
