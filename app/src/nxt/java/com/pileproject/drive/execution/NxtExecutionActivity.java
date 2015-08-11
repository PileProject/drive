/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.execution;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.pileproject.drive.comm.BluetoothCommunicator;
import com.pileproject.drive.util.SharedPreferencesWrapper;


public class NxtExecutionActivity extends ExecutionActivityBase {
	
	@Override
	protected void connectToDevice() {
		// Get mac address
		String address = SharedPreferencesWrapper.loadDefaultDeviceAddress(getApplicationContext());
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = adapter.getRemoteDevice(address);
		BluetoothCommunicator bt = new BluetoothCommunicator(device);
		
		showConnectionProgressDialog(); // Create a ProgressDialog
		
		// Try to connect
		new Thread(() -> {
            try {
                bt.open();

                // Inform this activity has already connected to nxt
                Intent intent = new Intent();
                intent.putExtra("is_connected", true);
                setResult(RESULT_OK, intent);

                startExecution();
            }
            catch (Exception e) {
                showConnectionFailedDialog();
            }
            dismissConnectionProgressDialog();
        }).start();
	}
	
	@Override
	protected DeviceController getDeviceController() {
		return new NxtController(new NxtControllerBuilder(this));
	}
}
