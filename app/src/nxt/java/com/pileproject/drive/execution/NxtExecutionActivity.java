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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.pileproject.drive.comm.BluetoothCommunicator;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drivecommand.model.nxt.NxtMachine;

import java.io.IOException;

import trikita.log.Log;

public class NxtExecutionActivity extends ExecutionActivityBase {
    private NxtMachine mMachine;

    @Override
    protected void connectToDevice() {
        // get the device's MAC address
        String address = MachinePreferences.get(getApplicationContext()).getMacAddress();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(address);
        try {
            mMachine = new NxtMachine(new BluetoothCommunicator(device));
        } catch (IOException e) {
            // this would not happen
            // because `getRemoteDevice(address)` above will throw an Exception
            // when `address` is invalid (= `device` is null)
            Log.d("Device should not be null.");
            throw new RuntimeException(e);
        }

        showConnectionProgressDialog(); // create a ProgressDialog

        // run an execution thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // try to connect
                    mMachine.connect();
                    startExecution();
                } catch (Exception e) {
                    showConnectionFailedDialog();
                }
                dismissConnectionProgressDialog();
            }
        }).start();
    }

    @Override
    protected MachineController getMachineController() {
        return new NxtController(mMachine);
    }
}
