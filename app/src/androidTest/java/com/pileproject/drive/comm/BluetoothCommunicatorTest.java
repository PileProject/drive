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

import android.bluetooth.BluetoothDevice;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class BluetoothCommunicatorTest {

    @Test(expected = IOException.class)
    public void whenBluetoothDeviceIsNull_thenThrowAnIOException() throws Exception {
        BluetoothDevice device = null;
        BluetoothCommunicator comm = new BluetoothCommunicator(device);

        comm.open();
    }

    @Test
    public void whenFirstOpenMethodFails_thenTrySecondMethod_thenThrowAnIOException() throws Exception {
        // TODO: write a good test
        // NOTE: can't mock/spy BluetoothDevice/BluetoothSocket
        //       because they are `final` classes
    }

    @Test
    public void whenSocketIsNotNull_thenCloseSucceeds() throws Exception {
        // TODO: write a good test
        // NOTE: can't mock/spy BluetoothSocket
        //       because it is a `final` class
    }

    @Test
    public void whenSocketIsAlreadyNull_thenCloseSucceeds() throws Exception {
        BluetoothCommunicator comm = new BluetoothCommunicator(null);
        comm.close();
    }

    @Test
    public void testWrite() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        BluetoothCommunicator comm = new BluetoothCommunicator(null);

        // use a mock instance
        Whitebox.setInternalState(comm, "mOutputStream", outputStream);

        // write null
        comm.write(null, 0);
        verify(outputStream).write(null);

        // write a data
        byte[] data = {0x00, 0x01, 0x02, };
        comm.write(data, 0);
        verify(outputStream).write(data);

    }

    @Test
    public void testRead() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        BluetoothCommunicator comm = new BluetoothCommunicator(null);

        // use a mock instance
        Whitebox.setInternalState(comm, "mInputStream", inputStream);

        // read null
        comm.read(0, 0);
        verify(inputStream).read(new byte[0]);

        // read a data
        comm.read(100, 0);
        verify(inputStream).read(new byte[100]);
    }
}