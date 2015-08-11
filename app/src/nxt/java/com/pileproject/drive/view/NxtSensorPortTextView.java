/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController;


public class NxtSensorPortTextView extends PortTextView {

	public NxtSensorPortTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static String getSensorName(Context context, int sensorType) {
		switch (sensorType) {
		case NxtController.SensorProperty.SENSOR_LIGHT:
			return context.getString(R.string.sensors_light);
		case NxtController.SensorProperty.SENSOR_SOUND:
			return context.getString(R.string.sensors_sound);
		case NxtController.SensorProperty.SENSOR_TOUCH:
			return context.getString(R.string.sensors_touch);
		}
		
		return "";
	}
	
	public static int getSensorColor(int sensorType) {
		switch (sensorType) {
		case NxtController.SensorProperty.SENSOR_LIGHT:
			return Color.rgb(50, 142, 183);
		case NxtController.SensorProperty.SENSOR_TOUCH:
			return Color.rgb(65, 163, 86);
		case NxtController.SensorProperty.SENSOR_SOUND:
			return Color.rgb(221, 86, 82);
		}
		
		return Color.GRAY;
	}
	
	@Override
	public void setAttachmentType(int attachmentType) {
		mAttachmentType = attachmentType;
		
		this.setText(getSensorName(mContext, attachmentType));
		this.setBackgroundColor(getSensorColor(attachmentType));
	}

	@Override
	public int getAttachmentType() {
		return mAttachmentType;
	}
}
