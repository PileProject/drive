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

import com.pileproject.drivecommand.machine.input.SoundSensor;
import com.pileproject.drivecommand.machine.input.TouchSensor;
import com.pileproject.drivecommand.machine.output.Motor;

import java.util.LinkedList;
import java.util.List;

public class NxtController implements DeviceController {
	public Motor rightMotor;
	public Motor leftMotor;
	public TouchSensor touchSensor;
	public LightSensor lightSensor;
	public SoundSensor soundSensor;
	public int rightMotorPower = INIT_MOTOR_POWER;
	public int leftMotorPower = INIT_MOTOR_POWER;
	
	public static final int MAX_MOTOR_POWER = 900;
	public static final int INIT_MOTOR_POWER = 500;
	
	public enum MotorDir {
		Forward, Neutral, Backward
	};
	
	public enum MotorKind {
		LeftMotor, RightMotor
	};
	
	public static final class SensorProperty {
		public static final int NUMBER_OF_SENSORS = 3;
		public static final int NUMBER_OF_SENSOR_PORTS = 4;
		public static final int SENSOR_UNUSED = 0;
		public static final int SENSOR_TOUCH = 1;
		public static final int SENSOR_LIGHT = 2;
		public static final int SENSOR_SOUND = 3;
		
		public static final class LightSensor {
			public static final int PctMin = 0;
			public static final int PctMax = 100;
			public static final int DEFAULT = 50;
		}
		
		public static final class SoundSensor {
			public static final int SI_dB_SiMin = 40;
			public static final int SI_dB_SiMax = 120;
			public static final int SI_dB_DEFAULT = 70;
		}
		
		public static List<Integer> getAllSensors() {
			List<Integer> sensors = new LinkedList<Integer>();
			
			sensors.add(SENSOR_TOUCH);
			sensors.add(SENSOR_LIGHT);
			sensors.add(SENSOR_SOUND);
			
			return sensors;
		}
	}
	
	public static final class MotorProperty {
		public static final int NUMBER_OF_MOTORS = 2;
		public static final int NUMBER_OF_MOTOR_PORTS = 3;
		public static final int MOTOR_UNUSED = 0;
		public static final int MOTOR_LEFT = 1;
		public static final int MOTOR_RIGHT = 2;
		
		public static List<Integer> getAllMotors() {
			List<Integer> motors = new LinkedList<Integer>();
			
			motors.add(MOTOR_LEFT);
			motors.add(MOTOR_RIGHT);
			
			return motors;
		}
	}
	
	public NxtController() {
		rightMotor = Motor.B;
		leftMotor = Motor.C;
		
		touchSensor = new TouchSensor(SensorPort.S1);
		soundSensor = new SoundSensor(SensorPort.S2);
		lightSensor = new LightSensor(SensorPort.S3);
	}
	
	public NxtController(NxtControllerBuilder builder) {
		setMotorPort(builder);
		setSensorPort(builder);
	}
	
	private void setMotorPort(NxtControllerBuilder builder) {
		Motor m;
		
		m = builder.getMotorPort(MotorProperty.MOTOR_LEFT);
		if (m == null) {
			leftMotor = builder.getFirstUnusedMotor(MotorProperty.MOTOR_LEFT);
		}
		else {
			leftMotor = m;
		}
		
		m = builder.getMotorPort(MotorProperty.MOTOR_RIGHT);
		if (m == null) {
			rightMotor = builder.getFirstUnusedMotor(MotorProperty.MOTOR_RIGHT);
		}
		else {
			rightMotor = m;
		}
	}
	
	private void setSensorPort(NxtControllerBuilder builder) {
		SensorPort s;

		s = builder.getSensorPort(SensorProperty.SENSOR_LIGHT);
		if (s == null) {
			lightSensor = new LightSensor(builder.getFirstUnusedSensorPort(SensorProperty.SENSOR_LIGHT));
		}
		else {
			lightSensor = new LightSensor(s);
		}

		s = builder.getSensorPort(SensorProperty.SENSOR_SOUND);
		if (s == null) {
			soundSensor = new SoundSensor(builder.getFirstUnusedSensorPort(SensorProperty.SENSOR_SOUND));
		}
		else {
			soundSensor = new SoundSensor(s);
		}

		s = builder.getSensorPort(SensorProperty.SENSOR_TOUCH);
		if (s == null) {
			touchSensor	= new TouchSensor(builder.getFirstUnusedSensorPort(SensorProperty.SENSOR_TOUCH));
		}
		else {
			touchSensor = new TouchSensor(s);
		}
	}
	
	/***
	 * move forward
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 */
	public void moveForward() {
		move(MotorDir.Forward, MotorDir.Forward);
	}
	
	/***
	 * move backward
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 */
	public void moveBackward() {
		move(MotorDir.Backward, MotorDir.Backward);
	}
	
	/***
	 * turn left
	 * motor's powers are set to the constant power
	 */
	public void turnLeft() {
		move(MotorDir.Backward, MotorDir.Forward);
	}
	
	/***
	 * turn right
	 * motor's powers are set to the constant power
	 */
	public void turnRight() {
		move(MotorDir.Forward, MotorDir.Backward);
	}
	
	/***
	 * halt motors
	 */
	public void halt() {
		move(MotorDir.Neutral, MotorDir.Neutral);
	}
	
	/***
	 * drive the both motors toward specified direction
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 * 
	 * @param leftMotorDir
	 * @param rightMotorDir
	 */
	public void move(MotorDir leftMotorDir, MotorDir rightMotorDir) {
		switch (leftMotorDir) {
			case Forward:
				leftMotor.setSpeed(leftMotorPower);
				leftMotor.forward();
				break;
			case Neutral:
				leftMotor.stop();
				break;
			case Backward:
				leftMotor.setSpeed(leftMotorPower);
				leftMotor.backward();
				break;
		}
		
		switch (rightMotorDir) {
			case Forward:
				rightMotor.setSpeed(rightMotorPower);
				rightMotor.forward();
				break;
			case Neutral:
				rightMotor.stop();
				break;
			case Backward:
				rightMotor.setSpeed(rightMotorPower);
				rightMotor.backward();
				break;
		}
	}
	
	/***
	 * set motor's power as percent
	 * 
	 * @param motor
	 * @param percent
	 */
	public void setMotorPower(MotorKind motor, double percent) {
		if (percent > 100.0) percent = 100.0;
		else if (percent < 0) percent = 0.0;
		
		switch (motor) {
			case LeftMotor:
				leftMotorPower = (int) ((MAX_MOTOR_POWER * percent) / 100.0);
				break;
			case RightMotor:
				rightMotorPower = (int) ((MAX_MOTOR_POWER * percent) / 100.0);
				break;
		}
	}
	
	public void finalize() throws RuntimeException {
		halt();
		touchSensor = null;
		soundSensor = null;
		lightSensor = null;
	}
}
