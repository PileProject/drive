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
package com.pileproject.drive.util.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.pileproject.drivecommand.machine.MachineBase;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * A class which produces Observable of RxJava.
 * This class is for handling bluetooth connection events.
 */
public class RxBluetoothConnector {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private RxBluetoothConnector() {
        throw new AssertionError("This class cannot be instantiated");
    }

    /**
     * Create {@link Observable} which will produce a stream
     * for connecting to the given bluetooth device.
     * The connection will be cut just after the connection established, since
     * the paring process can be done in that manner.
     * This function is run on a new thread.
     * @param bluetoothDevice A bluetooth device to be connected.
     * @return Observable with a bluetooth device stream
     */
    public static Observable<BluetoothDevice> pairing(final BluetoothDevice bluetoothDevice) {
        return Observable.create(new Observable.OnSubscribe<BluetoothDevice>() {
            @Override
            public void call(Subscriber<? super BluetoothDevice> subscriber) {
                try {
                    BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                    socket.connect();
                    socket.close();

                    subscriber.onNext(bluetoothDevice);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread()); // can't run on UI thread since this will cause ANR
    }

    /**
     * Create {@link Observable} which will produce a stream
     * for connecting to the given bluetooth device.
     * This function is run on a new thread.
     * @param bluetoothDevice A bluetooth device to be connected.
     * @return Observable with a bluetooth socket stream
     */
    public static Observable<BluetoothSocket> connect(final BluetoothDevice bluetoothDevice) {
        return Observable.create(new Observable.OnSubscribe<BluetoothSocket>() {
            @Override
            public void call(Subscriber<? super BluetoothSocket> subscriber) {
                try {
                    BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                    socket.connect();

                    subscriber.onNext(socket);
                    subscriber.onCompleted();
                } catch (IOException firstCause) {

                    try {
                        BluetoothSocket socket = connectByRfcommSocket(bluetoothDevice);
                        subscriber.onNext(socket);
                        subscriber.onCompleted();
                    } catch (RfcommSocketInvocationException e) {
                        Throwable throwable = new IOException("Failed to connect by \"createRfcommSocketToServiceRecord\" " +
                                "and invoke \"createRfcommSocket\" method", firstCause);

                        subscriber.onError(throwable);
                    } catch (IOException e) {
                        Throwable throwable = new IOException("Failed to connect by \"createRfcommSocket\"", e);

                        subscriber.onError(throwable);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    private static BluetoothSocket connectByRfcommSocket(BluetoothDevice bluetoothDevice) throws IOException {

        try {
            Method method = BluetoothDevice.class.getMethod("createRfcommSocket", int.class);
            BluetoothSocket socket = (BluetoothSocket) method.invoke(bluetoothDevice, 1);

            socket.connect();

            return socket;

        } catch (NoSuchMethodException e) {
            // because Android API 15 does not support collapsed catch clauses for these 3 exceptions,
            // this redundant code is needed (it's since API level 19).
            throw new RfcommSocketInvocationException("Unable to invoke BluetoothSocket#RfcommSocket:", e);
        } catch (IllegalAccessException e) {
            throw new RfcommSocketInvocationException("Unable to invoke BluetoothSocket#RfcommSocket:", e);
        } catch (InvocationTargetException e) {
            throw new RfcommSocketInvocationException("Unable to invoke BluetoothSocket#RfcommSocket:", e);
        }
    }

    private static class RfcommSocketInvocationException extends RuntimeException {
        RfcommSocketInvocationException(String message, Throwable e) {
            super(message, e);
        }
    }
}
