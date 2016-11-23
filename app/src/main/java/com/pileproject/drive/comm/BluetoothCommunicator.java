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
package com.pileproject.drive.comm;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;

import com.pileproject.drivecommand.model.com.ICommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import trikita.log.Log;


public class BluetoothCommunicator implements ICommunicator {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    public BluetoothCommunicator(@NonNull BluetoothDevice device) throws NullPointerException {
        if (device == null) {
            throw new NullPointerException("Device should not be null");
        }
        mDevice = device;
    }

    @Override
    public void open() throws IOException {
        // Orthodox method
        // This call may fail. It depends on the device.
        // Therefore, we do redundancy check with the below reflection method.
        mSocket = mDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        try {
            mSocket.connect();
        } catch (IOException firstIOException) {
            Log.d("Failed to connect with an orthodox method");
            try {
                // Redundancy check
                Method method = mDevice.getClass().getMethod("createRfcommSocket", int.class);
                mSocket = (BluetoothSocket) method.invoke(mDevice, 1);
                mSocket.connect();
            } catch (IOException secondIOException) {
                Log.d("Failed to connect with a redundancy method");
                // Unable to connect; close the socket and get out
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                    Log.d("it seems unable to recover");
                }
                throw secondIOException;
            } catch (Exception exception) {
                exception.printStackTrace();
                Log.d("this exception should not be occurred in release " + "version");
            }
        }

        mOutputStream = mSocket.getOutputStream();
        mInputStream = mSocket.getInputStream();
    }

    @Override
    public void close() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("Failed to close connection.", e);
            }
        }
        mSocket = null;
    }

    @Override
    public void write(byte[] request, int timeout) throws RuntimeException {
        // TODO:use timeout
        try {
            mOutputStream.write(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int length, int timeout) throws RuntimeException {
        // TODO:use timeout
        byte[] buffer = new byte[length];
        int numBytes;
        try {
            numBytes = mInputStream.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] result = new byte[numBytes];
        System.arraycopy(buffer, 0, result, 0, numBytes);

        return result;
    }
}