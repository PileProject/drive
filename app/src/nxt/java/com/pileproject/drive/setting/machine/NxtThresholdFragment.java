package com.pileproject.drive.setting.machine;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.programming.visual.block.selection.IfNXTIsOutOfLineBlock;
import com.pileproject.drive.programming.visual.block.selection.IfThereWasALargeSoundBlock;
import com.pileproject.drive.util.SharedPreferencesWrapper;

public class NxtThresholdFragment extends Fragment {
	private SeekBar mLightSensorSeek = null;
	private TextView mLightSensorText = null;

	private SeekBar mSoundSensorSeek = null;
	private TextView mSoundSensorText = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

		final Activity activity = getActivity();
		
		mLightSensorSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mLightSensorText.setText(getString(R.string.setting_threshold_unit_percent, progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SharedPreferencesWrapper.saveIntPreference(activity, IfNXTIsOutOfLineBlock.class.getName(), seekBar.getProgress());
			}
		});
		
		final int savedLightValue = SharedPreferencesWrapper.loadIntPreference(activity, IfNXTIsOutOfLineBlock.class.getName(), -1);
		mLightSensorSeek.setMax(NxtController.SensorProperty.LightSensor.PctMax);
		mLightSensorSeek.setProgress((savedLightValue == -1) ? NxtController.SensorProperty.LightSensor.DEFAULT : savedLightValue);
		
		mSoundSensorSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mSoundSensorText.setText(getString(R.string.setting_threshold_unit_dB, progress + NxtController.SensorProperty.SoundSensor.SI_dB_SiMin));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { } 

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SharedPreferencesWrapper.saveIntPreference(activity, IfThereWasALargeSoundBlock.class.getName(), seekBar.getProgress() + NxtController.SensorProperty.SoundSensor.SI_dB_SiMin);
			}
		});
		
		final int savedSoundValue = SharedPreferencesWrapper.loadIntPreference(activity, IfThereWasALargeSoundBlock.class.getName(), -1);
		mSoundSensorSeek.setMax(NxtController.SensorProperty.SoundSensor.SI_dB_SiMax - NxtController.SensorProperty.SoundSensor.SI_dB_SiMin);
		mSoundSensorSeek.setProgress((savedSoundValue == -1) ? NxtController.SensorProperty.SoundSensor.SI_dB_DEFAULT - NxtController.SensorProperty.SoundSensor.SI_dB_SiMin 
				: savedSoundValue -NxtController.SensorProperty.SoundSensor.SI_dB_SiMin);
	}
}
