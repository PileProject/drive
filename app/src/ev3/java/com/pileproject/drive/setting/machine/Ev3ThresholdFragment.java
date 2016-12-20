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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.machine.Ev3CarController.SensorProperty.ColorSensor;
import com.pileproject.drive.machine.Ev3CarController.SensorProperty.Rangefinder;
import com.pileproject.drive.preferences.BlockPreferences;

/**
 * A fragment for setting the thresholds of devices. This fragment will be used by {@link Ev3ThresholdPreference}.
 */
public class Ev3ThresholdFragment extends DialogFragment {
    public static final int COLOR_DEFAULT_THRESHOLD = 50;
    public static final int RANGEFINDER_DEFAULT_THRESHOLD = 128;

    private SeekBar mColorSensorSeekBar;
    private TextView mColorSensorText;

    private SeekBar mRangefinderSeekBar;
    private TextView mRangefinderText;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_threshold);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_ev3_threshold, container, false);

        mColorSensorSeekBar = (SeekBar) v.findViewById(R.id.setting_threshold_colorSensor);
        mColorSensorText = (TextView) v.findViewById(R.id.setting_threshold_colorSensorValueText);

        mRangefinderSeekBar = (SeekBar) v.findViewById(R.id.setting_threshold_rangefinder);
        mRangefinderText = (TextView) v.findViewById(R.id.setting_threshold_rangefinderValueText);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // NOTE: this is necessary because this screen collapsed on API 23+
        resizeDialog();

        mColorSensorSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mColorSensorText.setText(getString(R.string.setting_threshold_unit_percent,
                                                   progress + ColorSensor.PCT_MIN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BlockPreferences.get(getActivity())
                        .setColorSensorIlluminanceThreshold(seekBar.getProgress() + ColorSensor.PCT_MIN);
            }
        });

        int savedColorValue = BlockPreferences.get(getActivity()).getColorSensorIlluminanceThreshold(COLOR_DEFAULT_THRESHOLD);
        mColorSensorSeekBar.setMax(ColorSensor.PCT_MAX - ColorSensor.PCT_MIN);
        mColorSensorSeekBar.setProgress(savedColorValue - ColorSensor.PCT_MIN);

        mRangefinderSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRangefinderText.setText(getString(R.string.setting_threshold_unit_cm,
                                                   progress + Rangefinder.CM_MIN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BlockPreferences.get(getActivity())
                        .setRangefinderThreshold(seekBar.getProgress() + Rangefinder.CM_MIN);
            }
        });

        int savedRangefinderValue = BlockPreferences.get(getActivity()).getRangefinderThreshold(RANGEFINDER_DEFAULT_THRESHOLD);
        mRangefinderSeekBar.setMax(Rangefinder.CM_MAX - Rangefinder.CM_MIN);
        mRangefinderSeekBar.setProgress(savedRangefinderValue - Rangefinder.CM_MIN);
    }

    /**
     * This function should be called in {@link DialogFragment#onActivityCreated(Bundle)}.
     * Otherwise, the dialog size will never be changed
     */
    private void resizeDialog() {
        Dialog dialog = getDialog();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // resize window large enough to display list views
        int dialogWidth = (int) (metrics.widthPixels * 0.8);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
    }
}
