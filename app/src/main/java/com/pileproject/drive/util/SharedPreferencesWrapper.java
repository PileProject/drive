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

package com.pileproject.drive.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.pileproject.drive.app.DriveApplication;

/**
 * Wrapper class for preferences
 *
 * @author mandai
 */
public class SharedPreferencesWrapper {

    private static final String PREF_ADDRESS = "device_address";

    public static boolean saveStringPreference(String tag, String prf) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext());
        Editor editor = shared.edit();
        editor.putString(tag, prf);
        return editor.commit();
    }

    public static String loadStringPreference(String tag, String def) {
        return PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext()).getString(tag, def);
    }

    public static boolean saveIntPreference(String tag, int prf) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext());
        Editor editor = shared.edit();
        editor.putInt(tag, prf);
        return editor.commit();
    }

    public static int loadIntPreference(String tag, int def) {
        return PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext()).getInt(tag, def);
    }

    public static boolean saveBoolPreference(String tag, boolean prf) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext());
        Editor editor = shared.edit();
        editor.putBoolean(tag, prf);
        return editor.commit();
    }

    public static boolean loadBoolPreference(String tag, boolean def) {
        return PreferenceManager.getDefaultSharedPreferences(DriveApplication.getContext()).getBoolean(tag, def);
    }

    public static boolean saveDefaultDeviceAddress(String address) {
        return saveStringPreference(PREF_ADDRESS, address);
    }

    public static String loadDefaultDeviceAddress() {
        return loadStringPreference(PREF_ADDRESS, null);
    }
}
