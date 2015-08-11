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

package com.pileproject.drive.setting.machine;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.pileproject.drive.R;

/***
 * View class for ListView displaying devices in an activity setting default
 * device
 * 
 * @author yusaku
 * @version 1.0 4-June-2013
 */
public class DeviceListItemView extends LinearLayout implements Checkable {
	
	private RadioButton mRadioButton;
	
	private void initialize() {
		addView(inflate(getContext(), R.layout.view_devicelistitem, null));
		mRadioButton = (RadioButton) findViewById(R.id.deviceListItemView_radioButton);
	}
	
	public DeviceListItemView(Context context) {
		super(context);
		initialize();
	}
	
	public DeviceListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public DeviceListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	@Override
	public boolean isChecked() {
		return mRadioButton.isChecked();
	}
	
	@Override
	public void setChecked(boolean checked) {
		mRadioButton.setChecked(checked);
	}
	
	@Override
	public void toggle() {
	}
	
}
