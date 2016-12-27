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
package com.pileproject.drive.util.development;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * A class for checking building environments, devices, etc.
 */
public class DeployUtils {

    private DeployUtils() {
        throw new AssertionError("This class cannot be instantiated");
    }

    /**
     * Returns <code>true</code> if the device is emulator.
     */
    public static boolean isOnEmulator() {
        // from http://stackoverflow.com/questions/2799097/how-can-i-detect-when-an-android-application-is-running-in-the-emulator
        return Build.HARDWARE.contains("goldfish");
    }

    /**
     * Returns <code>true</code> if the building configuration is Debug.
     *
     * @param context a {@link Context} of the appliation
     */
    public static boolean isDebugMode(Context context) {
        PackageManager manager = context.getPackageManager();
        ApplicationInfo info;

        try {
            info = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return false;
        }

        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;

    }

    /**
     * Returns <code>true</code> if the building configuration is Release.
     *
     * @param context a {@link Context} of the application
     */
    public static boolean isReleaseMode(Context context) {
        return !isDebugMode(context);
    }
}
