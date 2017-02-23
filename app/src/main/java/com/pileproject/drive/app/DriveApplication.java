/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
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
package com.pileproject.drive.app;

import android.app.Application;
import android.content.Context;

import com.pileproject.drive.module.FlavorModule;

/**
 * An Application class for Drive Applications. This class provides some helper methods to share global information
 * such as {@link Context}.
 */
public class DriveApplication extends Application {
    private static DriveApplication sInstance;

    private DriveComponent mDriveComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initInjection();
    }

    /**
     * Gets a {@link Context} anywhere.
     *
     * @return a saved {@link Context}
     */
    public static synchronized Context getContext() {
        return sInstance.getApplicationContext();
    }

    /**
     * Gets a {@link DriveComponent}. The component will be used for build-flavor specific injections.
     *
     * @return a saved {@link DriveComponent}
     */
    public DriveComponent getAppComponent() {
        return mDriveComponent;
    }

    private void initInjection() {
        mDriveComponent = DaggerDriveComponent.builder()
                .flavorModule(new FlavorModule())
                .build();
    }
}

