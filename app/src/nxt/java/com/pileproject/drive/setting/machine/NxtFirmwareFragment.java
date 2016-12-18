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
package com.pileproject.drive.setting.machine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.MachinePreferences;

import static com.pileproject.drive.preferences.MachinePreferencesSchema.Firmware.LEJOS;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.Firmware.STANDARD;

public class NxtFirmwareFragment extends DialogFragment {
    private RadioGroup mFirmwareRadioGroup;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_firmware);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_nxt_firmware, container, false);
        mFirmwareRadioGroup = (RadioGroup) v.findViewById(R.id.firmware_firmwares);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirmwareRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.firmware_standard:
                        MachinePreferences.get(getActivity()).setFirmware(STANDARD);
                        break;
                    case R.id.firmware_lejos:
                        MachinePreferences.get(getActivity()).setFirmware(LEJOS);
                        break;
                }
            }
        });

        String firm = MachinePreferences.get(getActivity()).getFirmware();
        if (firm.equals(STANDARD))
            mFirmwareRadioGroup.check(R.id.firmware_standard);
        else
            mFirmwareRadioGroup.check(R.id.firmware_lejos);

    }
}
