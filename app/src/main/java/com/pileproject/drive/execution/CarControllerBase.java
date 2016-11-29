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

import com.pileproject.drivecommand.machine.MachineBase;
import com.pileproject.drivecommand.machine.device.input.ColorSensor;
import com.pileproject.drivecommand.machine.device.input.GyroSensor;
import com.pileproject.drivecommand.machine.device.input.LineSensor;
import com.pileproject.drivecommand.machine.device.input.Rangefinder;
import com.pileproject.drivecommand.machine.device.input.RemoteControlReceiver;
import com.pileproject.drivecommand.machine.device.input.SoundSensor;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.output.Buzzer;
import com.pileproject.drivecommand.machine.device.output.Led;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.machine.device.port.InputPort;
import com.pileproject.drivecommand.machine.device.port.OutputPort;

import java.util.LinkedList;
import java.util.List;

public class CarControllerBase implements MachineController {
    public static final int INIT_MOTOR_POWER = 60;

    protected MachineBase mMachine = null;

    protected Motor mLeftMotor = null;
    protected Motor mRightMotor = null;

    protected Buzzer mBuzzer = null;
    protected Led mLed = null;

    protected ColorSensor mColorSensor = null;
    protected GyroSensor mGyroSensor = null;
    protected LineSensor mLineSensor = null;
    protected Rangefinder mRangefinder = null;
    protected RemoteControlReceiver mRemoteControlReceiver = null;
    protected SoundSensor mSoundSensor = null;
    protected TouchSensor mTouchSensor = null;

    protected int mLeftMotorPower = INIT_MOTOR_POWER;
    protected int mRightMotorPower = INIT_MOTOR_POWER;

    public enum MotorDir {
        Forward, Neutral, Backward
    }

    public enum MotorKind {
        LeftMotor, RightMotor
    }

    // all input devices
    public class INPUT {
        public static final String NONE = "none";
        public static final String COLOR = "color_sensor";
        public static final String GYRO = "gyro_sensor";
        public static final String LINE = "line_sensor";
        public static final String RANGEFINDER = "rangefinder";
        public static final String REMOTE_CONTROL_RECEIVER = "remote_control_receiver";
        public static final String SOUND = "sound_sensor";
        public static final String TOUCH = "touch_sensor";
    }

    // all output devices
    public class OUTPUT {
        public static final String NONE = "none";
        public static final String LEFT = "left_motor";
        public static final String RIGHT = "right_motor";
        public static final String BUZZER = "buzzer";
        public static final String LED = "led";
    }

    /**
     * Internal class to contain motor properties
     * particularly for Android activities
     */
    public static final class MotorProperty {
        public static List<String> getAllMotors() {
            List<String> motors = new LinkedList<>();
            motors.add(OUTPUT.LEFT);
            motors.add(OUTPUT.RIGHT);
            return motors;
        }
    }

    @Override
    public void halt() {
        move(MotorDir.Neutral, MotorDir.Neutral);
    }

    @Override
    public void close() {
        halt();
        mMachine.disconnect();
    }

    public float[] getColorSensorRgb() {
        if (mColorSensor == null) return null;
        return mColorSensor.getRgb();
    }

    public int getColorSensorIlluminance() {
        if (mColorSensor == null) return -1;
        return mColorSensor.getIlluminance();
    }


    public int getGyroSensorRate() {
        if (mGyroSensor == null) return -1;
        return mGyroSensor.getRate();
    }

    public int getGyroSensorAngle() {
        if (mGyroSensor == null) return -1;
        return mGyroSensor.getAngle();
    }

    /**
     * Gets line sensor value
     *
     * @return light strength
     */
    public int getLineSensorValue() {
        if (mLineSensor == null) return -1;
        return mLineSensor.getSensorValue();
    }

    public int getRangefinderDisance() {
        if (mRangefinder == null) return -1;
        return mRangefinder.getDistance();
    }

    public int getRemoteControlReceiverButton() {
        if (mRemoteControlReceiver == null) return -1;
        return mRemoteControlReceiver.getRemoteButton();
    }

    public int getRemoteControlReceiverDistance() {
        if (mRemoteControlReceiver == null) return -1;
        return mRemoteControlReceiver.getRemoteDistance();
    }

    /**
     * Gets sound sensor value in dB
     *
     * @return dB
     */
    public int getSoundSensorDb() {
        if (mSoundSensor == null) return -1;
        return mSoundSensor.getDb();
    }

    /**
     * Gets touch sensor value
     *
     * @return is touched (true) or not (false)
     */
    public boolean isTouchSensorTouched() {
        if (mTouchSensor == null) return false;
        return mTouchSensor.isTouched();
    }

    public int getTouchSensorTouchedCount() {
        if (mTouchSensor == null) return -1;
        return mTouchSensor.getTouchedCount();
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
     * Drives the both motors toward specified direction
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
     *              (e.g., -10 to 0, 200 to 100).
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


    protected void connectOutputPort(String outputType, OutputPort port) {
        if (OUTPUT.NONE.equals(outputType)) return ;
        if (OUTPUT.LEFT.equals(outputType)) mLeftMotor = mMachine.createMotor(port);
        if (OUTPUT.RIGHT.equals(outputType)) mRightMotor = mMachine.createMotor(port);
        if (OUTPUT.BUZZER.equals(outputType)) mBuzzer = mMachine.createBuzzer(port);
        if (OUTPUT.LED.equals(outputType)) mLed = mMachine.createLed(port);
    }

    protected void connectInputPort(String sensorType, InputPort port) {
        if (INPUT.NONE.equals(sensorType)) return ;
        if (INPUT.COLOR.equals(sensorType)) mColorSensor = mMachine.createColorSensor(port);
        if (INPUT.GYRO.equals(sensorType)) mGyroSensor = mMachine.createGyroSensor(port);
        if (INPUT.LINE.equals(sensorType)) mLineSensor = mMachine.createLineSensor(port);
        if (INPUT.RANGEFINDER.equals(sensorType)) mRangefinder = mMachine.createRangefinder(port);
        if (INPUT.REMOTE_CONTROL_RECEIVER.equals(sensorType))
            mRemoteControlReceiver = mMachine.createRemoteControlReceiver(port);
        if (INPUT.SOUND.equals(sensorType)) mSoundSensor = mMachine.createSoundSensor(port);
        if (INPUT.TOUCH.equals(sensorType)) mTouchSensor = mMachine.createTouchSensor(port);
    }

}
