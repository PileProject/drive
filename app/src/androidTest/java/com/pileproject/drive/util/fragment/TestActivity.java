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

package com.pileproject.drive.util.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

/**
 * Created by yusaku on 2016/07/09.
 */
public class TestActivity extends AppCompatActivity implements AlertDialogFragment.EventListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout view = new LinearLayout(this);
        setContentView(view);

        new AlertDialogFragment.Builder(this)
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButtonLabel("OK")
                .setNegativeButtonLabel("CANCEL")
                .setTag("tag")
                .show();
    }

    public void showDialog(AlertDialogFragment.Builder builder) {
        builder.show();
    }

    @Override
    public void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params) {

    }

    @Override
    public void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params) {

    }
}
