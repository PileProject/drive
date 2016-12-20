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
package com.pileproject.drive.execution;

import android.support.test.runner.AndroidJUnit4;

import com.pileproject.drive.machine.CarControllerBase;
import com.pileproject.drive.machine.Ev3CarController;
import com.pileproject.drivecommand.machine.device.input.ColorSensor;
import com.pileproject.drivecommand.machine.device.input.Rangefinder;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.model.ev3.Ev3Machine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import static com.pileproject.drive.machine.CarControllerBase.MotorKind.LeftMotor;
import static com.pileproject.drive.machine.CarControllerBase.MotorKind.RightMotor;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class Ev3CarControllerTest {

    @Mock Ev3Machine machine;
    @InjectMocks
    Ev3CarController controller;

    @Mock TouchSensor touchSensor;
    @Mock Rangefinder rangefinder;
    @Mock ColorSensor colorSensor;

    @Mock Motor leftMotor;
    @Mock Motor rightMotor;

    @Before
    public void setUp() {
        machine = mock(Ev3Machine.class);
        controller = new Ev3CarController(machine);

        touchSensor = mock(TouchSensor.class);
        rangefinder = mock(Rangefinder.class);
        colorSensor = mock(ColorSensor.class);

        leftMotor = mock(Motor.class);
        rightMotor = mock(Motor.class);
    }

    private void setUpMotors() {
        Whitebox.setInternalState(controller, "mLeftMotor", leftMotor);
        Whitebox.setInternalState(controller, "mRightMotor", rightMotor);
    }


    // TODO: add tests related to preferences


    @Test
    public void whenTouchSensorIsNull_thenReturnFalse() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", null);

        assertFalse(controller.isTouchSensorTouched());
    }

    @Test
    public void whenTouchSensorIsNotNull_andTouchSensorIsTouched_thenReturnTrue() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", touchSensor);
        doReturn(true).when(touchSensor).isTouched();

        assertTrue(controller.isTouchSensorTouched());
    }

    @Test
    public void whenTouchSensorIsNotNull_andTouchSensorIsNotTouched_thenReturnFalse() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", touchSensor);
        doReturn(false).when(touchSensor).isTouched();

        assertFalse(controller.isTouchSensorTouched());
    }

    @Test
    public void whenRangefinderIsNull_thenReturnNegative() throws Exception {
        Whitebox.setInternalState(controller, "mRangefinder", null);

        assertEquals(controller.getRangefinderDistance(), -1);
    }

    @Test
    public void whenRangefinderIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mRangefinder", rangefinder);
        doReturn(10).when(rangefinder).getDistance();

        assertEquals(10, controller.getRangefinderDistance());
    }

    @Test
    public void whenColorSensorIsNull_thenReturnNegative() throws Exception {
        Whitebox.setInternalState(controller, "mColorSensor", null);

        assertEquals(controller.getColorSensorIlluminance(), -1);
    }

    @Test
    public void whenColorSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mColorSensor", colorSensor);
        doReturn(20).when(colorSensor).getIlluminance();

        assertEquals(20, controller.getColorSensorIlluminance());
    }

    @Test
    public void whenMachineMovesForward_thenMotorsMoveCorrectly() throws Exception {
        setUpMotors();

        controller.moveForward();

        verify(leftMotor).forward();
        verify(rightMotor).forward();
    }

    @Test
    public void whenMachineMovesBackward_thenMotorsMoveCorrectly() throws Exception {
        setUpMotors();

        controller.moveBackward();

        verify(leftMotor).backward();
        verify(rightMotor).backward();
    }

    @Test
    public void whenMachineTurnLeft_thenMotorsMoveCorrectly() throws Exception {
        setUpMotors();

        controller.turnLeft();

        verify(leftMotor).backward();
        verify(rightMotor).forward();
    }

    @Test
    public void whenMachineTurnRight_thenMotorsMoveCorrectly() throws Exception {
        setUpMotors();

        controller.turnRight();

        verify(leftMotor).forward();
        verify(rightMotor).backward();
    }

    @Test
    public void whenMachineHalt_thenMotorsStopCorrectly() throws Exception {
        setUpMotors();

        controller.halt();

        verify(leftMotor).stop();
        verify(rightMotor).stop();
    }

    @Test
    public void whenMotorSpeedsAreNotInitialized_thenMovesForwardWithDefaultValue() throws Exception {
        setUpMotors();

        controller.moveForward();

        verify(leftMotor).setSpeed(CarControllerBase.MotorProperty.INIT_MOTOR_POWER);
        verify(rightMotor).setSpeed(CarControllerBase.MotorProperty.INIT_MOTOR_POWER);
    }

    @Test
    public void whenSetMotorPowerCalled_thenMovesForwardWithTheValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(LeftMotor, 10);
        controller.setMotorPower(RightMotor, 10);

        controller.moveForward();

        verify(leftMotor).setSpeed(10);
        verify(rightMotor).setSpeed(10);
    }

    @Test
    public void whenSetMotorPowerCalledWithOverUpperBoundValue_thenMovesForwardWithUpperBoundValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(LeftMotor, 200);
        controller.setMotorPower(RightMotor, 200);

        controller.moveForward();

        verify(leftMotor).setSpeed(100);
        verify(rightMotor).setSpeed(100);
    }

    @Test
    public void whenSetMotorPowerCalledWithUnderLowerBoundValue_thenMovesForwardWithLowerBoundValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(LeftMotor, -10);
        controller.setMotorPower(RightMotor, -10);

        controller.moveForward();

        verify(leftMotor).setSpeed(0);
        verify(rightMotor).setSpeed(0);
    }

    @Test
    public void whenControllerCloses_thenMachineDisconnects() throws Exception {
        controller.close();

        verify(machine).disconnect();
    }
}
