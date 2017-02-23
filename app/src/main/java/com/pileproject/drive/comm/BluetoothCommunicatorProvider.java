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
package com.pileproject.drive.comm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drivecommand.model.com.ICommunicator;

/**
 * An implementation of {@link CommunicatorProvider} that provides a concrete {@link ICommunicator} for Bluetooth
 * connections.
 */
public class BluetoothCommunicatorProvider implements CommunicatorProvider {

    private static final BluetoothCommunicatorProvider INSTANCE = new BluetoothCommunicatorProvider();

    public static BluetoothCommunicatorProvider getInstance() {
        return INSTANCE;
    }

    private BluetoothCommunicatorProvider() {

    }

    @Override
    public ICommunicator getCommunicator() {

        // get the device's MAC address
        String address = MachinePreferences.get(DriveApplication.getContext()).getMacAddress();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(address);
        return new BluetoothCommunicator(device);
    }
}
