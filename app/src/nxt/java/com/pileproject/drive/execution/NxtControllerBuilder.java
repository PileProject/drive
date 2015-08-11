/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.execution;

import android.content.Context;
import android.util.SparseArray;

import com.pileproject.drive.util.SharedPreferencesWrapper;
import com.pileproject.drivecommand.machine.output.Motor;

import java.util.Deque;
import java.util.LinkedList;




public class NxtControllerBuilder {
	private SparseArray<SensorPort> mSensorPorts = null;
	private SparseArray<Motor> mMotors = null;
	
	private Deque<SensorPort> mUnusedSensorPorts = null;
	private Deque<Motor> mUnusedMotors = null;

	public NxtControllerBuilder(Context context) {
		mSensorPorts = new SparseArray<SensorPort>();
		mMotors = new SparseArray<Motor>();
		
		mUnusedSensorPorts = new LinkedList<SensorPort>();
		mUnusedMotors = new LinkedList<Motor>();
		
		loadSensorsFromPreferences(context);
		loadMotorsFromPreferences(context);
	}
	
	public SensorPort getSensorPort(int sensorType) {
		return mSensorPorts.get(sensorType);
	}

	public Motor getMotorPort(int motorType) {
		return mMotors.get(motorType);
	}

	public SensorPort getFirstUnusedSensorPort(int sensorType) {
		return mUnusedSensorPorts.pop();
	}
	
	public Motor getFirstUnusedMotor(int motorType) {
		return mUnusedMotors.pop();
	}
	
	private void loadSensorsFromPreferences(Context context) {
		final int notAssigned = NxtController.SensorProperty.SENSOR_UNUSED;

		final SensorPort[] sensorPorts = { SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4 };
		final String[] sensorPortPrefTags = { "sensorPort1", "sensorPort2", "sensorPort3", "sensorPort4" };

		for (int i = 0; i < sensorPortPrefTags.length; ++ i) {
			int key = SharedPreferencesWrapper.loadIntPreference(context, sensorPortPrefTags[i], notAssigned);
			
			if (key == notAssigned) {
				mUnusedSensorPorts.push(sensorPorts[i]);
				continue;
			}
			
			mSensorPorts.put(key, sensorPorts[i]);
		}
	}
	
	private void loadMotorsFromPreferences(Context context) {
		final int notAssigned = NxtController.MotorProperty.MOTOR_UNUSED;
		
		final Motor[] motors = { Motor.A, Motor.B, Motor.C };
		final String[] motorPortPrefTags = { "motorPortA", "motorPortB", "motorPortC" };
		
		for (int i = 0; i < motorPortPrefTags.length; ++ i) {
			int key = SharedPreferencesWrapper.loadIntPreference(context, motorPortPrefTags[i], notAssigned);
			
			if (key == notAssigned) {
				mUnusedMotors.add(motors[i]);
				continue;
			}
			
			mMotors.put(key, motors[i]);
		}
	}
}
