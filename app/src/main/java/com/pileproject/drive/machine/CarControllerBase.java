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
package com.pileproject.drive.machine;

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

import java.util.Arrays;
import java.util.List;

import static com.pileproject.drive.machine.CarControllerBase.MotorProperty.INIT_MOTOR_POWER;

/**
 * This is a base machine controller which controls a car-formed machine (e.g., Nxt/Ev3/Pile robot). A car-formed
 * machine is a machine which has {@link Motor}s (left/right) and arbitrary number of other devices (e.g.,
 * {@link Buzzer}, {@link TouchSensor}). Therefore, the information (methods/properties) related to such devices (e.g.,
 * should be treated in child controllers like {@link NxtCarController}.
 *
 * This base class specifies the interface to control a car-formed machine as methods which are the union of
 * methods of child controllers, and provides the list of all input/output devices which may be used (see:
 * {@link InputDevice}, {@link OutputDevice}).
 * NOTE: Because of the design of the controllers, if you want to add a new controller with a new feature which extends
 * this base class, you should add the methods which you defined in your new controller to this base class.
 *
 * Because a car-formed machine must have two motors (left/right), this base class has the properties of these
 * motors (see: {@link CarControllerBase.MotorProperty}). However, as mentioned above, the information of other
 * devices should been treated in child controllers. For example, we keep the sensor properties of Nxt as
 * {@link NxtCarController.SensorProperty}.
 *
 * To provide a hint to users, please override {@see #getAllInputDevices()}, {@see #getAllOutputDevices()} in your
 * child classes.
 */
public abstract class CarControllerBase implements MachineController {
    protected MachineBase mMachine;

    protected Motor mLeftMotor;
    protected Motor mRightMotor;

    protected Buzzer mBuzzer;
    protected Led mLed;

    protected ColorSensor mColorSensor;
    protected GyroSensor mGyroSensor;
    protected LineSensor mLineSensor;
    protected Rangefinder mRangefinder;
    protected RemoteControlReceiver mRemoteControlReceiver;
    protected SoundSensor mSoundSensor;
    protected TouchSensor mTouchSensor;

    protected int mLeftMotorPower = INIT_MOTOR_POWER;
    protected int mRightMotorPower = INIT_MOTOR_POWER;

    /**
     * An internal class that contains all the input devices as string constants. These constants will be used to
     * show what kinds of devices this machine has or connect devices with ports, etc.
     */
    public static class InputDevice {
        public static final String NONE = "none";
        public static final String COLOR = "color_sensor";
        public static final String GYRO = "gyro_sensor";
        public static final String LINE = "line_sensor";
        public static final String RANGEFINDER = "rangefinder";
        public static final String REMOTE_CONTROL_RECEIVER = "remote_control_receiver";
        public static final String SOUND = "sound_sensor";
        public static final String TOUCH = "touch_sensor";
    }

    /**
     * An internal class that contains all the output devices as string constants. These constants will be used to
     * show what kinds of devices this machine has or connect devices with ports, etc.
     */
    public static class OutputDevice {
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
     * An internal class that contains motor properties.
     */
    public static final class MotorProperty {
        public static final int INIT_MOTOR_POWER = 60;
        public static final List<String> ALL_MOTORS = Arrays.asList(
                        OutputDevice.LEFT_MOTOR,
                        OutputDevice.RIGHT_MOTOR
                );
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

    /**
     * Gets the list of available input devices (i.e., sensors).
     *
     * @return a list of input devices
     */
    public abstract List<String> getAllInputDevices();

    /**
     * Gets the color in RGB from a {@link ColorSensor}.
     *
     * @return color in RGB (<code>float[0:2] = {R, G, B}</code>)
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public float[] getColorSensorRgb() {
        throw new UnsupportedOperationException("This machine does not support 'getColorSensorRgb' command");
    }

    /**
     * Gets the light strength from a {@link ColorSensor}.
     *
     * @return light strength
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getColorSensorIlluminance() {
        throw new UnsupportedOperationException("This machine does not support 'getColorSensorIlluminance' command");
    }

    /**
     * Gets the moving rate of a {@link GyroSensor}.
     *
     * @return rate
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getGyroSensorRate() {
        throw new UnsupportedOperationException("This machine does not support 'getGyroSensorRate' command");
    }

    /**
     * Gets the angle of a {@link GyroSensor}.
     *
     * @return angle
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getGyroSensorAngle() {
        throw new UnsupportedOperationException("This machine does not support 'getGyroSensorAngle' command");
    }

    /**
     * Gets the light strength from a {@link LineSensor}.
     *
     * @return light strength
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getLineSensorValue() {
        throw new UnsupportedOperationException("This machine does not support 'getLineSensorValue' command");
    }

    /**
     * Gets the distance to an obstacle from a {@link Rangefinder}.
     *
     * @return distance
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getRangefinderDistance() {
        throw new UnsupportedOperationException("This machine does not support 'getRangefinderDistance' command");
    }

    /**
     * Gets the button number of RemoteController of a {@link RemoteControlReceiver}.
     *
     * @return button number
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getRemoteControlReceiverButton() {
        throw new UnsupportedOperationException("This machine does not support 'getRemoteControlReceiverButton' command");
    }

    /**
     * Gets the distance between a {@link RemoteControlReceiver} attached to this machine and a RemoteController.
     *
     * @return distance
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getRemoteControlReceiverDistance() {
        throw new UnsupportedOperationException("This machine does not support 'getRemoteControlReceiverDistance' command");
    }

    /**
     * Gets the sound volume from a {@link SoundSensor}.
     *
     * @return sound volume in dB
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getSoundSensorDb() {
        throw new UnsupportedOperationException("This machine does not support 'getSoundSensorDb' command");
    }

    /**
     * Checks a {@link TouchSensor} is currently touched or not.
     *
     * @return is touched (true) or not (false)
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public boolean isTouchSensorTouched() {
        throw new UnsupportedOperationException("This machine does not support 'isTouchSensorTouched' command");
    }

    /**
     * Gets the touched count of a {@link TouchSensor}.
     *
     * @return the number of touched count
     * @exception UnsupportedOperationException
     *              if the sensor is unsupported by this machine
     */
    public int getTouchSensorTouchedCount() {
        throw new UnsupportedOperationException("This machine does not support 'getTouchSensorTouchedCount' command");
    }


    /**
     * Gets the list of available output devices (e.g., motors/buzzer/led).
     *
     * @return a list of output devices
     */
    public abstract List<String> getAllOutputDevices();

    /**
     * Turns on a {@link Buzzer}. Use {@see #turnOffBuzzer()} to stop it.
     *
     * @exception UnsupportedOperationException
     *              if buzzer is unsupported by this machine
     */
    public void turnOnBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'turnOnBuzzer' command");
    }

    /**
     * Turns off a {@link Buzzer}. Use {@see #turnOnBuzzer()} to make it rings.
     *
     * @exception UnsupportedOperationException
     *              if buzzer is unsupported by this machine
     */
    public void turnOffBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'turnOffBuzzer' command");
    }

    /**
     * Turns on and off a {@link Buzzer} alternately. Use {@see #turnOffBuzzer()} to stop it.
     *
     * @exception UnsupportedOperationException
     *              if buzzer is unsupported by this machine
     */
    public void beepBuzzer() {
        throw new UnsupportedOperationException("This machine does not support 'beepBuzzer' command");
    }

    /**
     * Turns on a {@link Led}. Use {@see #turnOffLed()} to stop it.
     *
     * @exception UnsupportedOperationException
     *              if LED is unsupported by this machine
     */
    public void turnOnLed() {
        throw new UnsupportedOperationException("This machine does not support 'turnOnLed' command");
    }

    /**
     * Turns off a {@link Led}. Use {@see #turnOnLed()} to make it turn on.
     *
     * @exception UnsupportedOperationException
     *              if LED is unsupported by this machine
     */
    public void turnOffLed() {
        throw new UnsupportedOperationException("This machine does not support 'turnOffLed' command");
    }

    /**
     * Moves motors forward. The power of the left and right motors can be set by
     * {@see #setMotorPower()} with a {@link MotorKind} argument.
     */
    public void moveForward() {
        move(MotorDir.Forward, MotorDir.Forward);
    }

    /**
     * Moves motors backward. The power of the left and right motors can be set by
     * {@see #setMotorPower()} with a {@link MotorKind} argument.
     */
    public void moveBackward() {
        move(MotorDir.Backward, MotorDir.Backward);
    }

    /**
     * Turns left (the left and right motor move backward and forward respectively). The power of the left /
     * right motors are set to the constant power.
     */
    public void turnLeft() {
        move(MotorDir.Backward, MotorDir.Forward);
    }

    /**
     * Turns right (the left and right motor move forward and backward respectively). The power of the left /
     * right motors are set to the constant power.
     */
    public void turnRight() {
        move(MotorDir.Forward, MotorDir.Backward);
    }

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
     * A setter of motor power in percentage.
     *
     * @param kind see {@link MotorKind} to specify the kind of a motor
     * @param percent power of motor in a range <code>[0, 100]</code>
     *              if an out of bound value is passed, then the value will be clipped
     */
    public void setMotorPower(MotorKind kind, int percent) {
        if (percent > 100) {
            percent = 100;
        } else if (percent < 0) {
            percent = 0;
        }

        switch (kind) {
            case LeftMotor:
                mLeftMotorPower = percent;
                break;
            case RightMotor:
                mRightMotorPower = percent;
                break;
        }
    }


    protected void connectOutputPort(String outputType, OutputPort port) {
        if (OutputDevice.NONE.equals(outputType)) return ;
        if (OutputDevice.LEFT_MOTOR.equals(outputType)) mLeftMotor = mMachine.createMotor(port);
        if (OutputDevice.RIGHT_MOTOR.equals(outputType)) mRightMotor = mMachine.createMotor(port);
        if (OutputDevice.BUZZER.equals(outputType)) mBuzzer = mMachine.createBuzzer(port);
        if (OutputDevice.LED.equals(outputType)) mLed = mMachine.createLed(port);
    }

    protected void connectInputPort(String sensorType, InputPort port) {
        if (InputDevice.NONE.equals(sensorType)) return ;
        if (InputDevice.COLOR.equals(sensorType)) mColorSensor = mMachine.createColorSensor(port);
        if (InputDevice.GYRO.equals(sensorType)) mGyroSensor = mMachine.createGyroSensor(port);
        if (InputDevice.LINE.equals(sensorType)) mLineSensor = mMachine.createLineSensor(port);
        if (InputDevice.RANGEFINDER.equals(sensorType)) mRangefinder = mMachine.createRangefinder(port);
        if (InputDevice.REMOTE_CONTROL_RECEIVER.equals(sensorType))
            mRemoteControlReceiver = mMachine.createRemoteControlReceiver(port);
        if (InputDevice.SOUND.equals(sensorType)) mSoundSensor = mMachine.createSoundSensor(port);
        if (InputDevice.TOUCH.equals(sensorType)) mTouchSensor = mMachine.createTouchSensor(port);
    }

}
