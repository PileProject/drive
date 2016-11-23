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
package com.pileproject.drive.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.CommonPreferences;

/**
 * Setting activity
 * <p/>
 * This activity contains setting fragments
 *
 * @author yusaku
 * @version 1.0 4-June-2013
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        toolbar.setTitle(R.string.setting_label);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.setting_content, new SettingFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    public static class SettingFragment extends PreferenceFragmentCompat {

        private CheckBoxPreference mSupervisorPreference;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            if (preference instanceof DialogPreferenceInterface) {
                ((DialogPreferenceInterface) preference).startDialog(this);
                return;
            }

            super.onDisplayPreferenceDialog(preference);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mSupervisorPreference = (CheckBoxPreference) getPreferenceManager().findPreference("supervisor_preference");
            mSupervisorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    CommonPreferences.get(getActivity()).setSupervisorMode((boolean) newValue);

                    // newValue is not set to the component (e.g., checkbox) if return false
                    return true;
                }
            });

            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    /**
     * Interface for dialog-based preferences which extends {@link DialogPreference}.
     * DialogPreference used in {@link SettingFragment} should implement this interface.
     */
    public interface DialogPreferenceInterface {

        /**
         * Called in {@link SettingFragment#onDisplayPreferenceDialog(Preference)}
         * The function should show a fragment which represents the preference of this class.
         *
         * @param parent parent fragment, which should be {@link SettingFragment}
         */
        void startDialog(PreferenceFragmentCompat parent);
    }
}
