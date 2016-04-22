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

package com.pileproject.drive.setting.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.CommonPreferences;

public class SupervisorModeFragment extends Fragment {
    private CheckBox mEnableDebugModeCheckBox;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_supervisor_mode, container, false);

        mEnableDebugModeCheckBox = (CheckBox) v.findViewById(R.id.supervisorMode_enableSupervisorMode);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        boolean isEnabledSupervisorMode = CommonPreferences.get(getActivity()).getSupervisorMode();

        mEnableDebugModeCheckBox.setChecked(isEnabledSupervisorMode);
        mEnableDebugModeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                boolean isChecked = checkBox.isChecked();

                CommonPreferences.get(getActivity()).setSupervisorMode(isChecked);
            }
        });
    }
}