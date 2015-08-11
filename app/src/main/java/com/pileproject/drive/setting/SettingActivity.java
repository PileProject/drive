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

package com.pileproject.drive.setting;

import android.preference.PreferenceActivity;

import com.pileproject.drive.R;

import java.util.List;

/***
 * Setting activity
 * 
 * This activity contains setting fragments
 * 
 * @author yusaku
 * @version 1.0 4-June-2013
 */
public class SettingActivity extends PreferenceActivity {
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);
		// add back button to header list
		Header backHeader = new Header();
		backHeader.id = R.string.back;
		backHeader.titleRes = R.string.back;
		
		target.add(backHeader);
	}
	
	@Override
	public void onHeaderClick(Header header, int position) {
		if (header.id == R.string.back) {
			finish();
		}
		
		super.onHeaderClick(header, position);
	}
	
	@Override
	protected boolean isValidFragment(String fragmentName) {
		return false;
	}
}
