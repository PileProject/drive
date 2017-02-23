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
package com.pileproject.drive.title;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.activity.ProgrammingActivity;
import com.pileproject.drive.setting.SettingActivity;

/**
 * A class of the title page. Users can move from this page to the programming page ({@link ProgrammingActivity}) or
 * preference page ({@link SettingActivity}).
 */
public class TitleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        // a button to move to programming page
        findViewById(R.id.title_startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ProgrammingActivity.createIntent(getApplicationContext()));
            }
        });

        // a button to move to preference page
        findViewById(R.id.title_settingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SettingActivity.createIntent(getApplicationContext()));
            }
        });

        setupToolbar();
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.title_toolbar);
        toolbar.setLogo(R.drawable.icon_launcher);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }
}
