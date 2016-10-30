/*
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

package com.pileproject.drive.app;

import android.app.Application;
import android.content.Context;

import com.pileproject.drive.module.FlavorModule;

/**
 * DriveApplication class
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 15-March-2016
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

    public static synchronized Context getContext() {
        return sInstance.getApplicationContext();
    }

    public DriveComponent getAppComponent() {
        return mDriveComponent;
    }

    private void initInjection() {
        mDriveComponent = DaggerDriveComponent.builder()
                .flavorModule(new FlavorModule())
                .build();
    }
}

