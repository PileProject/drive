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
package com.pileproject.drive.setting.machine;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.AttributeSet;

import com.pileproject.drive.setting.SettingActivity;

/**
 * A preference class for selecting the firmware of a machine. This class creates {@link NxtFirmwareFragment}.
 */
public class NxtFirmwarePreference extends DialogPreference implements SettingActivity.DialogPreferenceInterface {

    private static final String FRAGMENT_TAG = "nxt_firmware_preference";

    public NxtFirmwarePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NxtFirmwarePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NxtFirmwarePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NxtFirmwarePreference(Context context) {
        super(context);
    }

    @Override
    public void startDialog(PreferenceFragmentCompat parent) {
        if (parent.getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG) != null) {
            return;
        }

        DialogFragment f = new NxtFirmwareFragment();
        f.setTargetFragment(parent, 0);

        // not 'getFragmentManager' because we are creating
        // a nested DialogFragment
        f.show(parent.getChildFragmentManager(), FRAGMENT_TAG);
    }
}
