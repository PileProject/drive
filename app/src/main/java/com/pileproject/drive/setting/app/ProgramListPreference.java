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
package com.pileproject.drive.setting.app;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.AttributeSet;

import com.pileproject.drive.setting.SettingActivity;

/**
 * Created by yusaku on 2016/07/10.
 */
public class ProgramListPreference extends DialogPreference implements SettingActivity.DialogPreferenceInterface {

    private static final String FRAGMENT_TAG = "program_list_preference";

    public ProgramListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ProgramListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProgramListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgramListPreference(Context context) {
        super(context);
    }

    @Override
    public void startDialog(PreferenceFragmentCompat parent) {
        if (parent.getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG) != null) {
            return;
        }

        DialogFragment f = new ProgramListFragment();
        f.setTargetFragment(parent, 0);
        f.show(parent.getChildFragmentManager(), FRAGMENT_TAG);
    }
}
