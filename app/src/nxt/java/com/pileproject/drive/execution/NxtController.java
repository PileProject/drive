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

package com.pileproject.drive.execution;

import com.pileproject.drivecommand.machine.device.input.LineSensor;
import com.pileproject.drivecommand.machine.device.input.SoundSensor;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.model.nxt.NxtMachine;
import com.pileproject.drivecommand.model.nxt.port.NxtInputPort;
import com.pileproject.drivecommand.model.nxt.port.NxtOutputPort;

import java.util.LinkedList;
import java.util.List;

public class NxtController implements MachineController {
    public static final int MAX_MOTOR_POWER = 900;
    public static final int INIT_MOTOR_POWER = 500;
    public static final String TAG_SENSOR_PORT_1 = "sensorPort1";
    public static final String TAG_SENSOR_PORT_2 = "sensorPort2";
    public static final String TAG_SENSOR_PORT_3 = "sensorPort3";
    public static final String TAG_SENSOR_PORT_4 = "sensorPort4";
    public static final String TAG_MOTOR_PORT_A = "motorPortA";
    public static final String TAG_MOTOR_PORT_B = "motorPortB";
    public static final String TAG_MOTOR_PORT_C = "motorPortC";
    private NxtMachine mMachine = null;
    private Motor mRightMotor = null;
    private Motor mLeftMotor = null;
    private TouchSensor mTouchSensor = null;
    private LineSensor mLineSensor = null;
    private SoundSensor mSoundSensor = null;
    private int mRightMotorPower = INIT_MOTOR_POWER;
    private int mLeftMotorPower = INIT_MOTOR_POWER;

    /**
     * Default constructor.
     * binds each sensor and motor to their default port
     */
    public NxtController(NxtMachine machine) {
        mMachine = machine;
        mRightMotor = machine.createMotor(NxtOutputPort.PORT_B);
        mLeftMotor = machine.createMotor(NxtOutputPort.PORT_C);

        mTouchSensor = machine.createTouchSensor(NxtInputPort.PORT_1);
        mSoundSensor = machine.createSoundSensor(NxtInputPort.PORT_2);
        mLineSensor = machine.createLineSensor(NxtInputPort.PORT_3);
    }

    public NxtController(NxtMachine machine, NxtControllerBuilder builder) {
        mMachine = machine;
        setMotorPort(builder);
        setSensorPort(builder);
    }

    private void setMotorPort(NxtControllerBuilder builder) {
        NxtOutputPort motorPort;

        motorPort = builder.getMotorPort(MotorProperty.MOTOR_LEFT);
        if (motorPort != null) {
            mLeftMotor = mMachine.createMotor(motorPort);
        }

        motorPort = builder.getMotorPort(MotorProperty.MOTOR_RIGHT);
        if (motorPort != null) {
            mRightMotor = mMachine.createMotor(motorPort);
        }
    }

    private void setSensorPort(NxtControllerBuilder builder) {
        NxtInputPort sensorPort;

        sensorPort = builder.getSensorPort(SensorProperty.SENSOR_LINE);
        if (sensorPort != null) {
            mLineSensor = mMachine.createLineSensor(sensorPort);
        }

        sensorPort = builder.getSensorPort(SensorProperty.SENSOR_SOUND);
        if (sensorPort != null) {
            mSoundSensor = mMachine.createSoundSensor(sensorPort);
        }

        sensorPort = builder.getSensorPort(SensorProperty.SENSOR_TOUCH);
        if (sensorPort != null) {
            mTouchSensor = mMachine.createTouchSensor(sensorPort);
        }
    }

    /**
     * get touch sensor value
     *
     * @return is touched or not
     */
    public boolean getTouchSensorValue() {
        return mTouchSensor.isTouched();
    }

    /**
     * get sound sensor value in dB
     *
     * @return dB
     */
    public int getSoundSensorValue() {
        return mSoundSensor.getDb();
    }

    /**
     * get line sensor value
     *
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
    @Override
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
                    mLeftMotor.setSpeed(mLeftMotorPower);
                    mLeftMotor.forward();
                    break;
                case Neutral:
                    mLeftMotor.stop();
                    break;
                case Backward:
                    mLeftMotor.setSpeed(mLeftMotorPower);
                    mLeftMotor.backward();
                    break;
            }
        }

        if (mRightMotor != null) {
            switch (rightMotorDir) {
                case Forward:
                    mRightMotor.setSpeed(mRightMotorPower);
                    mRightMotor.forward();
                    break;
                case Neutral:
                    mRightMotor.stop();
                    break;
                case Backward:
                    mRightMotor.setSpeed(mRightMotorPower);
                    mRightMotor.backward();
                    break;
            }
        }
    }

    /**
     * set motor's power as percent
     *
     * @param kind
     * @param percent
     */
    public void setMotorPower(MotorKind kind, double percent) {
        if (percent > 100.0) percent = 100.0;
        else if (percent < 0) percent = 0.0;

        switch (kind) {
            case LeftMotor:
                mLeftMotorPower = (int) ((MAX_MOTOR_POWER * percent) / 100.0);
                break;
            case RightMotor:
                mRightMotorPower = (int) ((MAX_MOTOR_POWER * percent) / 100.0);
                break;
        }
    }

    @Override
    public void finalize() throws RuntimeException {
        halt();
        mTouchSensor = null;
        mSoundSensor = null;
        mLineSensor = null;
    }

    public enum MotorDir {
        Forward, Neutral, Backward
    }

    public enum MotorKind {
        LeftMotor, RightMotor
    }

    /**
     * internal class to contain sensor properties
     * particularly for Android activities
     */
    public static final class SensorProperty {
        public static final int NUMBER_OF_SENSORS = 3;
        public static final int NUMBER_OF_SENSOR_PORTS = 4;
        public static final int SENSOR_UNUSED = 0;
        public static final int SENSOR_TOUCH = 1;
        public static final int SENSOR_LINE = 2;
        public static final int SENSOR_SOUND = 3;

        public static List<Integer> getAllSensors() {
            List<Integer> sensors = new LinkedList<>();

            sensors.add(SENSOR_TOUCH);
            sensors.add(SENSOR_LINE);
            sensors.add(SENSOR_SOUND);

            return sensors;
        }

        public static final class LineSensor {
            public static final int PctMin = 0;
            public static final int PctMax = 100;
            public static final int DEFAULT = 50;
        }

        public static final class SoundSensor {
            public static final int SI_dB_SiMin = 40;
            public static final int SI_dB_SiMax = 120;
            public static final int SI_dB_DEFAULT = 70;
        }
    }

    /**
     * internal class to contain motor properties
     * particularly for Android activities
     */
    public static final class MotorProperty {
        public static final int NUMBER_OF_MOTORS = 2;
        public static final int NUMBER_OF_MOTOR_PORTS = 3;
        public static final int MOTOR_UNUSED = 0;
        public static final int MOTOR_LEFT = 1;
        public static final int MOTOR_RIGHT = 2;

        public static List<Integer> getAllMotors() {
            List<Integer> motors = new LinkedList<>();

            motors.add(MOTOR_LEFT);
            motors.add(MOTOR_RIGHT);

            return motors;
        }
    }
}
