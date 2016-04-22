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

package com.pileproject.drive.setting.machine;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController.SensorProperty.LineSensor;
import com.pileproject.drive.execution.NxtController.SensorProperty.SoundSensor;
import com.pileproject.drive.preferences.BlockPreferences;

public class NxtThresholdFragment extends PreferenceFragment {
    private SeekBar mLightSensorSeek = null;
    private TextView mLightSensorText = null;

    private SeekBar mSoundSensorSeek = null;
    private TextView mSoundSensorText = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_nxtthreshold, container, false);

        mLightSensorSeek = (SeekBar) v.findViewById(R.id.setting_threshold_lightSensor);
        mLightSensorText = (TextView) v.findViewById(R.id.setting_threshold_lightSensorValueText);

        mSoundSensorSeek = (SeekBar) v.findViewById(R.id.setting_threshold_soundSensor);
        mSoundSensorText = (TextView) v.findViewById(R.id.setting_threshold_soundSensorValueText);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLightSensorSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLightSensorText.setText(getString(R.string.setting_threshold_unit_percent,
                                                   progress + LineSensor.PctMin));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BlockPreferences.get(getActivity())
                        .setLineSensorThreshold(seekBar.getProgress() + LineSensor.PctMin);
            }
        });

        final int savedLightValue = BlockPreferences.get(getActivity()).getLineSensorThreshold();
        mLightSensorSeek.setMax(LineSensor.PctMax - LineSensor.PctMin);
        mLightSensorSeek.setProgress(savedLightValue - LineSensor.PctMin);

        mSoundSensorSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSoundSensorText.setText(getString(R.string.setting_threshold_unit_dB,
                                                   progress + SoundSensor.SI_dB_SiMin));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BlockPreferences.get(getActivity())
                        .setSoundSensorThreshold(seekBar.getProgress() + SoundSensor.SI_dB_SiMin);
            }
        });

        final int savedSoundValue = BlockPreferences.get(getActivity()).getSoundSensorThreshold();
        mSoundSensorSeek.setMax(SoundSensor.SI_dB_SiMax - SoundSensor.SI_dB_SiMin);
        mSoundSensorSeek.setProgress(savedSoundValue - SoundSensor.SI_dB_SiMin);
    }
}
