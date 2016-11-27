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
package com.pileproject.drive.execution;

import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.preferences.MachinePreferencesSchema;
import com.pileproject.drivecommand.machine.device.input.LineSensor;
import com.pileproject.drivecommand.machine.device.input.SoundSensor;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.model.nxt.NxtMachine;
import com.pileproject.drivecommand.model.nxt.port.NxtInputPort;
import com.pileproject.drivecommand.model.nxt.port.NxtOutputPort;

import java.util.LinkedList;
import java.util.List;

import static com.pileproject.drive.preferences.MachinePreferencesSchema.MOTOR.LEFT;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.MOTOR.RIGHT;

import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.TOUCH;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.SOUND;
import static com.pileproject.drive.preferences.MachinePreferencesSchema.SENSOR.LINE;

public class NxtController implements MachineController {
    public static final int MAX_MOTOR_POWER = 100;
    public static final int INIT_MOTOR_POWER = 60;
    private NxtMachine mMachine = null;
    private Motor mRightMotor = null;
    private Motor mLeftMotor = null;
    private TouchSensor mTouchSensor = null;
    private LineSensor mLineSensor = null;
    private SoundSensor mSoundSensor = null;
    private int mRightMotorPower = INIT_MOTOR_POWER;
    private int mLeftMotorPower = INIT_MOTOR_POWER;

    /**
     * binds each sensor and motor to their port
     */
    public NxtController(NxtMachine machine) {
        mMachine = machine;

        MachinePreferences preferences = MachinePreferences.get(DriveApplication.getContext());

        // connect motors
        connectMotor(preferences.getOutputPortA(), NxtOutputPort.PORT_A);
        connectMotor(preferences.getOutputPortB(), NxtOutputPort.PORT_B);
        connectMotor(preferences.getOutputPortC(), NxtOutputPort.PORT_C);

        // connect sensors
        connectSensor(preferences.getInputPort1(), NxtInputPort.PORT_1);
        connectSensor(preferences.getInputPort2(), NxtInputPort.PORT_2);
        connectSensor(preferences.getInputPort3(), NxtInputPort.PORT_3);
        connectSensor(preferences.getInputPort4(), NxtInputPort.PORT_4);
    }

    private void connectMotor(String motorType, NxtOutputPort port) {
        if (MachinePreferencesSchema.MOTOR.NONE.equals(motorType)) return ;
        if (LEFT.equals(motorType)) mLeftMotor = mMachine.createMotor(port);
        if (RIGHT.equals(motorType)) mRightMotor = mMachine.createMotor(port);
    }

    private void connectSensor(String sensorType, NxtInputPort port) {
        if (MachinePreferencesSchema.SENSOR.NONE.equals(sensorType)) return ;
        if (TOUCH.equals(sensorType)) mTouchSensor = mMachine.createTouchSensor(port);
        if (SOUND.equals(sensorType)) mSoundSensor = mMachine.createSoundSensor(port);
        if (LINE.equals(sensorType)) mLineSensor = mMachine.createLineSensor(port);
    }

    /**
     * get touch sensor value
     *
     * @return is touched or not
     */
    public boolean getTouchSensorValue() {
        if (mTouchSensor == null) return false;
        return mTouchSensor.isTouched();
    }

    /**
     * get sound sensor value in dB
     *
     * @return dB
     */
    public int getSoundSensorValue() {
        if (mSoundSensor == null) return -1;
        return mSoundSensor.getDb();
    }

    /**
     * get line sensor value
     *
     * @return light strength
     */
    public int getLineSensorValue() {
        if (mLineSensor == null) return -1;
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
    private void move(MotorDir leftMotorDir, MotorDir rightMotorDir) {
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
     * A setter of motor power.
     *
     * @param kind see {@link MotorKind}
     * @param power [0, 100]
     *              If an out of bound value is passed, then the value will be clipped
     *              (e.g., -10 -> 0, 200 -> 100).
     */
    public void setMotorPower(MotorKind kind, int power) {
        if (power > 100) {
            power = 100;
        } else if (power < 0) {
            power = 0;
        }

        switch (kind) {
            case LeftMotor:
                mLeftMotorPower = power;
                break;
            case RightMotor:
                mRightMotorPower = power;
                break;
        }
    }

    @Override
    public void close() {
        halt();
        mMachine.disconnect();
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
        public static List<String> getAllSensors() {
            List<String> sensors = new LinkedList<>();
            sensors.add(TOUCH);
            sensors.add(SOUND);
            sensors.add(LINE);
            return sensors;
        }

        public static final class LineSensor {
            public static final int PctMin = 0;
            public static final int PctMax = 100;
        }

        public static final class SoundSensor {
            public static final int SI_dB_SiMin = 40;
            public static final int SI_dB_SiMax = 120;
        }
    }

    /**
     * internal class to contain motor properties
     * particularly for Android activities
     */
    public static final class MotorProperty {
        public static List<String> getAllMotors() {
            List<String> motors = new LinkedList<>();
            motors.add(LEFT);
            motors.add(RIGHT);
            return motors;
        }
    }
}
