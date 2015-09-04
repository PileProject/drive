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

import com.pileproject.drivecommand.machine.Machine;
import com.pileproject.drivecommand.machine.device.input.SoundSensor;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.input.LineSensor;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.model.nxt.port.NxtInputPort;
import com.pileproject.drivecommand.model.nxt.port.NxtOutputPort;

import java.util.LinkedList;
import java.util.List;

public class NxtController implements MachineController {
	private NxtMachine mMachine = null;
	private Motor mRightMotor = null;
	private Motor mLeftMotor = null;
	private TouchSensor mTouchSensor = null;
	private LineSensor mLineSensor = null;
	private SoundSensor mSoundSensor = null;

	private int mRightMotorPower = NxtMachine.INIT_MOTOR_POWER;
	private int mLeftMotorPower = NxtMachine.INIT_MOTOR_POWER;

	public static String TAG_SENSOR_PORT_1 = "sensorPort1";
	public static String TAG_SENSOR_PORT_2 = "sensorPort2";
	public static String TAG_SENSOR_PORT_3 = "sensorPort3";
	public static String TAG_SENSOR_PORT_4 = "sensorPort4";

	public static String TAG_MOTOR_PORT_A = "motorPortA";
	public static String TAG_MOTOR_PORT_B = "motorPortB";
	public static String TAG_MOTOR_PORT_C = "motorPortC";

	public enum MotorDir {
		Forward, Neutral, Backward
	};

	public enum MotorKind {
		LeftMotor, RightMotor
	};

	/**
	 * Default constructor.
	 * binds each sensor and motor to their default port
	 */
	public NxtController(NxtMachine machine) {
		mMachine = machine;
		rightMotor = machine.createMotor(NxtMotorPort.PORT_B);
		leftMotor = machine.createMotor(MotorPort.PORT_C);
		
		touchSensor = machine.createTouchSensor(NxtSensorPort.PORT_1);
		soundSensor = machine.createSoundSensor(NxtSensorPort.PORT_2);
		lineSensor = machine.createLineSensor(NxtSensorPort.PORT_3);
	}
	
	public NxtController(NxtMachine machine, NxtControllerBuilder builder) {
		mMachine = machine;
		setMotorPort(builder);
		setSensorPort(builder);
	}
	
	private void setMotorPort(NxtControllerBuilder builder) {
		NxtOutputPort motorPort;

		motorPort = builder.getMotorPort(NxtMachine.MotorProperty.MOTOR_LEFT);
		if (motorPort != null) {
			mLeftMotor = mMachine.createMotor(motorPort);
		}

		motorPort = builder.getMotorPort(NxtMachine.MotorProperty.MOTOR_RIGHT);
		if (motorPort != null) {
			mRightMotor = mMachine.createMotor();
		}
	}
	
	private void setSensorPort(NxtControllerBuilder builder) {
		NxtInputPort sensorPort;

		sensorPort = builder.getSensorPort(NxtMachine.SensorProperty.SENSOR_LINE);
		if (sensorPort != null) {
			mLineSensor = mMachine.createLineSensor(sensorPort);
		}

		sensorPort = builder.getSensorPort(NxtMachine.SensorProperty.SENSOR_SOUND);
        if (sensorPort != null) {
			mSoundSensor = mMachine.createSoundSensor(sensorPort);
		}

		sensorPort = builder.getSensorPort(NxtMachine.SensorProperty.SENSOR_TOUCH);
		if (sensorPort != null) {
			mTouchSensor = mMachine.createTouchSensor(sensorPort);
		}
	}

	/**
	 * get touch sensor value
	 * @return is touched or not
	 */
	public boolean getTouchSensorValue() {
		return mTouchSensor.isTouched();
	}

	/**
	 * get sound sensor value in dB
	 * @return dB
	 */
	public int getSoundSensorValue() {
		return mSoundSensor.getDb();
	}

	/**
	 * get line sensor value
	 * @return light strength
	 */
	public int getLineSensorValue() {
		return mLineSensor.getSensorValue();
	}

	/**
	 * move forward
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 */
	public void moveForward() {
		move(MotorDir.Forward, MotorDir.Forward);
	}
	
	/**
	 * move backward
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 */
	public void moveBackward() {
		move(MotorDir.Backward, MotorDir.Backward);
	}
	
	/**
	 * turn left
	 * motor's powers are set to the constant power
	 */
	public void turnLeft() {
		move(MotorDir.Backward, MotorDir.Forward);
	}
	
	/**
	 * turn right
	 * motor's powers are set to the constant power
	 */
	public void turnRight() {
		move(MotorDir.Forward, MotorDir.Backward);
	}
	
	/**
	 * halt motors
	 */
	public void halt() {
		move(MotorDir.Neutral, MotorDir.Neutral);
	}
	
	/**
	 * drive the both motors toward specified direction
	 * public field variables leftMotorPower and rightMotorPower are set as
	 * motor's power
	 * 
	 * @param leftMotorDir
	 * @param rightMotorDir
	 */
	public void move(MotorDir leftMotorDir, MotorDir rightMotorDir) {
		if (mLeftMotor != null) {
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
		}

		if (mRightMotor != null) {
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
	}
	
	/**
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
				mLeftMotorPower = (int) ((NxtMachine.MAX_MOTOR_POWER * percent) / 100.0);
				break;
			case RightMotor:
				mRightMotorPower = (int) ((MAX_MOTOR_POWER * percent) / 100.0);
				break;
		}
	}
	
	public void finalize() throws RuntimeException {
		halt();
		mTouchSensor = null;
		mSoundSensor = null;
		mLineSensor = null;
	}
}
