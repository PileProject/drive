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

import android.support.test.runner.AndroidJUnit4;

import com.pileproject.drivecommand.machine.device.input.LineSensor;
import com.pileproject.drivecommand.machine.device.input.SoundSensor;
import com.pileproject.drivecommand.machine.device.input.TouchSensor;
import com.pileproject.drivecommand.machine.device.output.Motor;
import com.pileproject.drivecommand.model.nxt.NxtMachine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class NxtControllerTest {

    @Mock NxtMachine machine;
    @InjectMocks NxtController controller;

    @Mock TouchSensor touchSensor;
    @Mock SoundSensor soundSensor;
    @Mock LineSensor lineSensor;

    @Mock Motor leftMotor;
    @Mock Motor rightMotor;

    @Before
    public void setUp() {
        machine = mock(NxtMachine.class);
        controller = new NxtController(machine);

        touchSensor = mock(TouchSensor.class);
        soundSensor = mock(SoundSensor.class);
        lineSensor = mock(LineSensor.class);

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

        assertFalse(controller.getTouchSensorValue());
    }

    @Test
    public void whenTouchSensorIsNotNull_andTouchSensorIsTouched_thenReturnTrue() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", touchSensor);
        doReturn(true).when(touchSensor).isTouched();

        assertTrue(controller.getTouchSensorValue());
    }

    @Test
    public void whenTouchSensorIsNotNull_andTouchSensorIsNotTouched_thenReturnFalse() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", touchSensor);
        doReturn(false).when(touchSensor).isTouched();

        assertFalse(controller.getTouchSensorValue());
    }

    @Test
    public void whenSoundSensorIsNull_thenReturnNegative() throws Exception {
        Whitebox.setInternalState(controller, "mSoundSensor", null);

        assertEquals(controller.getSoundSensorValue(), -1);
    }

    @Test
    public void whenSoundSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mSoundSensor", soundSensor);
        doReturn(10).when(soundSensor).getDb();

        assertEquals(10, controller.getSoundSensorValue());
    }

    @Test
    public void whenLineSensorIsNull_thenReturnNegative() throws Exception {
        Whitebox.setInternalState(controller, "mLineSensor", null);

        assertEquals(controller.getLineSensorValue(), -1);
    }

    @Test
    public void whenLineSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mLineSensor", lineSensor);
        doReturn(20).when(lineSensor).getSensorValue();

        assertEquals(20, controller.getLineSensorValue());
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

        verify(leftMotor).setSpeed(NxtController.INIT_MOTOR_POWER);
        verify(rightMotor).setSpeed(NxtController.INIT_MOTOR_POWER);
    }

    @Test
    public void whenSetMotorPowerCalled_thenMovesForwardWithTheValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(NxtController.MotorKind.LeftMotor, 10);
        controller.setMotorPower(NxtController.MotorKind.RightMotor, 10);

        controller.moveForward();

        verify(leftMotor).setSpeed(10);
        verify(rightMotor).setSpeed(10);
    }

    @Test
    public void whenSetMotorPowerCalledWithOverUpperBoundValue_thenMovesForwardWithUpperBoundValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(NxtController.MotorKind.LeftMotor, 200);
        controller.setMotorPower(NxtController.MotorKind.RightMotor, 200);

        controller.moveForward();

        verify(leftMotor).setSpeed(100);
        verify(rightMotor).setSpeed(100);
    }

    @Test
    public void whenSetMotorPowerCalledWithUnderLowerBoundValue_thenMovesForwardWithLowerBoundValue() throws Exception {
        setUpMotors();

        controller.setMotorPower(NxtController.MotorKind.LeftMotor, -10);
        controller.setMotorPower(NxtController.MotorKind.RightMotor, -10);

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