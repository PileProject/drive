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

import static com.pileproject.drive.execution.CarControllerBase.MotorProperty.INIT_MOTOR_POWER;

/**
 * A machine controller which controls a car-formed machine. A car-formed machine is a machine that at least has
 * left and right {@link Motor}s. It depends on the machine what kinds of devices (e.g., {@link Buzzer}, {@link TouchSensor}
 * it actually has, and such information (e.g,. methods/properties) should be treated in child controllers like
 * {@link NxtCarController}.
 *
 * This base class must specify the interface to control a car-formed machine as methods which are the union of
 * methods of child controllers, and provide the list of all input/output devices may be used (see: {@link INPUT},
 * {@link OUTPUT}).
 * NOTE: Because of this, if you want add a new controller with a new feature which extends this base class , you
 * should add a new one in this base class.
 *
 * Because a car-formed machine must have two motors (left and right), this base class has the properties of these
 * motors (see: {@link com.pileproject.drive.execution.CarControllerBase.MotorProperty}). However, as mentioned
 * above, the information of other devices should have been specified in child controllers. For example, we keep the
 * sensor properties of Nxt as {@link com.pileproject.drive.execution.NxtCarController.SensorProperty}.
 */
public class CarControllerBase implements MachineController {
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
        public static final String LEFT_MOTOR = "left_motor";
        public static final String RIGHT_MOTOR = "right_motor";
        public static final String BUZZER = "buzzer";
        public static final String LED = "led";
    }

    public enum MotorDir {
        Forward, Neutral, Backward
    }

    public enum MotorKind {
        LeftMotor, RightMotor
    }

    /**
     * Internal class that contains motor properties.
     */
    public static final class MotorProperty {
        public static final int INIT_MOTOR_POWER = 60;

        public static List<String> getAllMotors() {
            List<String> motors = new LinkedList<>();
            motors.add(OUTPUT.LEFT_MOTOR);
            motors.add(OUTPUT.RIGHT_MOTOR);
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
        throw new UnsupportedOperationException("This machine does not support 'getColorSensorRgb' command");
    }

    public int getColorSensorIlluminance() {
        throw new UnsupportedOperationException("This machine does not support 'getColorSensorIlluminance' command");
    }


    public int getGyroSensorRate() {
        throw new UnsupportedOperationException("This machine does not support 'getGyroSensorRate' command");
    }

    public int getGyroSensorAngle() {
        throw new UnsupportedOperationException("This machine does not support 'getGyroSensorAngle' command");
    }

    /**
     * Gets line sensor value
     *
     * @return light strength
     */
    public int getLineSensorValue() {
        throw new UnsupportedOperationException("This machine does not support 'getLineSensorValue' command");
    }

    /**
     * Gets the distance between a machine and a Rangefinder
     * @return distance
     */
    public int getRangefinderDisance() {
        throw new UnsupportedOperationException("This machine does not support 'getRangefinderDisance' command");
    }

    public int getRemoteControlReceiverButton() {
        throw new UnsupportedOperationException("This machine does not support 'getRemoteControlReceiverButton' command");
    }

    public int getRemoteControlReceiverDistance() {
        throw new UnsupportedOperationException("This machine does not support 'getRemoteControlReceiverDistance' command");
    }

    /**
     * Gets sound sensor value in dB
     *
     * @return dB
     */
    public int getSoundSensorDb() {
        throw new UnsupportedOperationException("This machine does not support 'getSoundSensorDb' command");
    }

    /**
     * Checks the touch sensor is touched or not now.
     *
     * @return is touched (true) or not (false)
     */
    public boolean isTouchSensorTouched() {
        throw new UnsupportedOperationException("This machine does not support 'isTouchSensorTouched' command");
    }

    public int getTouchSensorTouchedCount() {
        throw new UnsupportedOperationException("This machine does not support 'getTouchSensorTouchedCount' command");
    }


    public void turnOnBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'turnOnBuzzer' command");
    }

    public void turnOffBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'turnOffBuzzer' command");
    }

    public void beepBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'beepBuzzer' command");
    }

    public void turnOnLed() {
        throw new UnsupportedOperationException("This machine does not support 'turnOnLed' command");
    }

    public void turnOffLed() {
        throw new UnsupportedOperationException("This machine does not support 'turnOffLed' command");
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
        if (OUTPUT.LEFT_MOTOR.equals(outputType)) mLeftMotor = mMachine.createMotor(port);
        if (OUTPUT.RIGHT_MOTOR.equals(outputType)) mRightMotor = mMachine.createMotor(port);
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
