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

package com.pileproject.drive.comm;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BluetoothCommunicatorTest {

    @Test(expected = NullPointerException.class)
    public void whenBluetoothDeviceIsNull_thenThrowNullPointerException() throws Exception {
        new BluetoothCommunicator(null);
    }

    // TODO: consider using PowerMock

    // @Test
    // public void whenFirstOpenMethodFails_thenTrySecondMethod_thenThrowIOException() throws Exception {
    //     // TODO: write a good test
    //     // NOTE: can't mock/spy BluetoothDevice/BluetoothSocket
    //     //       because they are `final` classes
    // }

    // @Test
    // public void whenSocketIsNotNull_thenCloseSucceeds() throws Exception {
    //     // TODO: write a good test
    //     // NOTE: can't mock/spy BluetoothSocket
    //     //       because it is a `final` class
    // }

    // @Test
    // public void whenSocketIsAlreadyNull_thenCloseSucceeds() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     comm.close();
    // }

    // @Test
    // public void whenOutputIsNull_thenWritesNothing() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     OutputStream outputStream = mock(OutputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mOutputStream", outputStream);

    //     // write null
    //     comm.write(null, 0);
    //     verify(outputStream).write(null);
    // }

    // @Test
    // public void whenOutputIsArray_thenWritesTheArray() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     OutputStream outputStream = mock(OutputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mOutputStream", outputStream);

    //     // write a data
    //     byte[] data = {0x00, 0x01, 0x02, };
    //     comm.write(data, 0);
    //     verify(outputStream).write(data);
    // }

    // @Test(expected = RuntimeException.class)
    // public void whenWriteFails_thenThrowRuntimeException() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     OutputStream outputStream = mock(OutputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // write a data
    //     byte[] data = {0x00, 0x01, 0x02, };

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mOutputStream", outputStream);
    //     doThrow(IOException.class).when(outputStream).write(data);

    //     comm.write(data, 0);
    // }

    // @Test
    // public void whenInputIsNull_thenReadNothing() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     InputStream inputStream = mock(InputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mInputStream", inputStream);

    //     // read null
    //     comm.read(0, 0);
    //     verify(inputStream).read(new byte[0]);
    // }

    // @Test
    // public void whenInputIsArray_thenReadTheArray() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     InputStream inputStream = mock(InputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mInputStream", inputStream);

    //     // read a data
    //     comm.read(100, 0);
    //     verify(inputStream).read(new byte[100]);
    // }

    // @Test(expected = RuntimeException.class)
    // public void whenReadFails_thenThrowRuntimeException() throws Exception {
    //     // TODO: mock BluetoothDevice
    //     InputStream inputStream = mock(InputStream.class);
    //     BluetoothCommunicator comm = new BluetoothCommunicator(null);

    //     // use a mock instance
    //     Whitebox.setInternalState(comm, "mInputStream", inputStream);
    //     doThrow(IOException.class).when(inputStream).read(new byte[100]);

    //     // read a data
    //     comm.read(100, 0);
    //     verify(inputStream).read(new byte[100]);
    // }
}
