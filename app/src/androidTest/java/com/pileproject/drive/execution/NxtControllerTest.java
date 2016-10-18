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
import org.mockito.internal.util.reflection.Whitebox;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class NxtControllerTest {

    NxtMachine machine;
    NxtController controller;

    TouchSensor touchSensor;
    SoundSensor soundSensor;
    LineSensor lineSensor;

    Motor leftMotor;
    Motor rightMotor;

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
        doCallRealMethod().when(leftMotor).setSpeed(anyInt());
        doCallRealMethod().when(leftMotor).getSpeed();

        Whitebox.setInternalState(controller, "mRightMotor", rightMotor);
        doCallRealMethod().when(rightMotor).setSpeed(anyInt());
        doCallRealMethod().when(rightMotor).getSpeed();
    }


    // TODO: add tests related to preferences


    @Test
    public void whenTouchSensorIsNull_thenReturnFalse() throws Exception {
        assertFalse(controller.getTouchSensorValue());
    }

    @Test
    public void whenTouchSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mTouchSensor", touchSensor);

        doReturn(true).when(touchSensor).isTouched();
        assertTrue(controller.getTouchSensorValue());

        doReturn(false).when(touchSensor).isTouched();
        assertFalse(controller.getTouchSensorValue());
    }

    @Test
    public void whenSoundSensorIsNull_thenReturnNegative() throws Exception {
        assertEquals(controller.getSoundSensorValue(), -1);
    }

    @Test
    public void whenSoundSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mSoundSensor", soundSensor);

        doReturn(10).when(soundSensor).getDb();
        assertEquals(10, controller.getSoundSensorValue());

        doReturn(100).when(soundSensor).getDb();
        assertEquals(100, controller.getSoundSensorValue());

        doReturn(-10).when(soundSensor).getDb();
        assertEquals(-10, controller.getSoundSensorValue());
    }

    @Test
    public void whenLineSensorIsNull_thenReturnNegative() throws Exception {
        assertEquals(controller.getLineSensorValue(), -1);
    }

    @Test
    public void whenLineSensorIsNotNull_thenReturnProperValue() throws Exception {
        Whitebox.setInternalState(controller, "mLineSensor", lineSensor);

        doReturn(20).when(lineSensor).getSensorValue();
        assertEquals(20, controller.getLineSensorValue());

        doReturn(200).when(lineSensor).getSensorValue();
        assertEquals(200, controller.getLineSensorValue());

        doReturn(-20).when(lineSensor).getSensorValue();
        assertEquals(-20, controller.getLineSensorValue());
    }

    @Test
    public void testMoveForward() throws Exception {
        setUpMotors();

        controller.moveForward();

        verify(leftMotor).forward();
        verify(rightMotor).forward();
    }

    @Test
    public void testMoveBackward() throws Exception {
        setUpMotors();

        controller.moveBackward();

        verify(leftMotor).backward();
        verify(rightMotor).backward();
    }

    @Test
    public void testTurnLeft() throws Exception {
        setUpMotors();

        controller.turnLeft();

        verify(leftMotor).backward();
        verify(rightMotor).forward();
    }

    @Test
    public void testTurnRight() throws Exception {
        setUpMotors();

        controller.turnRight();

        verify(leftMotor).forward();
        verify(rightMotor).backward();
    }

    @Test
    public void testHalt() throws Exception {
        setUpMotors();

        controller.halt();

        verify(leftMotor).stop();
        verify(rightMotor).stop();
    }

    @Test
    public void testSetMotorPower() throws Exception {
        setUpMotors();

        // at first, they have not been initialized
        assertEquals(0, leftMotor.getSpeed());
        assertEquals(0, rightMotor.getSpeed());

        controller.moveForward();

        // after a move command, they will be initialized with the default values
        assertEquals(NxtController.INIT_MOTOR_POWER, leftMotor.getSpeed());
        assertEquals(NxtController.INIT_MOTOR_POWER, rightMotor.getSpeed());

        controller.setMotorPower(NxtController.MotorKind.LeftMotor, 100);
        controller.setMotorPower(NxtController.MotorKind.RightMotor, 100);

        controller.moveForward(); // same as above

        assertEquals(NxtController.MAX_MOTOR_POWER, leftMotor.getSpeed());
        assertEquals(NxtController.MAX_MOTOR_POWER, rightMotor.getSpeed());
    }

    @Test
    public void testFinalize() throws Exception {
        controller.finalize();

        verify(machine).disconnect();
    }
}