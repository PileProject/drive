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

package com.pileproject.drive.title;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.pileproject.drive.R;
import com.pileproject.drive.database.ProgramDataManager;

/**
 * Title Screen
 * Users can move to Programming Screen or Device Selection Screen.
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public abstract class TitleActivityBase extends AppCompatActivity {

    private ProgramDataManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create new DB Manager
        mManager = new ProgramDataManager(getApplicationContext());
        mManager.deleteAll(); // Initialize program data
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_toolbar);

        toolbar.setLogo(R.drawable.icon_launcher);
        toolbar.setTitle(R.string.app_name);

        setSupportActionBar(toolbar);
    }

}