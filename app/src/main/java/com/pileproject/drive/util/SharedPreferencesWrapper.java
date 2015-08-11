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

package com.pileproject.drive.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/***
 * Wrapper class for preferences
 * 
 * @author mandai
 *
 */
public class SharedPreferencesWrapper {
	
	private static final String PREF_ADDRESS = "device_address";
	
	public static boolean saveStringPreference(Context context, String tag, String prf) {
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = shared.edit();
		editor.putString(tag, prf);
		return editor.commit();
	}
	
	public static String loadStringPreference(Context context, String tag, String def) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(tag, def);
	}
	
	public static boolean saveIntPreference(Context context, String tag, int prf) {
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = shared.edit();
		editor.putInt(tag, prf);
		return editor.commit();
	}
	
	public static int loadIntPreference(Context context, String tag, int def) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(tag, def);
	}
	
	public static boolean saveBoolPreference(Context context, String tag, boolean prf) {
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = shared.edit();
		editor.putBoolean(tag, prf);
		return editor.commit();
	}
	
	public static boolean loadBoolPreference(Context context, String tag, boolean def) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(tag, def);
	}
	
	public static boolean saveDefaultDeviceAddress(Context context, String address) {
		return saveStringPreference(context, PREF_ADDRESS, address);
	}
	
	public static String loadDefaultDeviceAddress(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ADDRESS, null);
	}
}
