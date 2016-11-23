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
package com.pileproject.drive.util.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Wrapper class of {@link RxBroadcastReceiver} for handling bluetooth events
 */
public class RxBluetoothBroadcastReceiver {

    /**
     * Create {@link Observable} which will produce a stream
     * when BluetoothDevice.ACTION_FOUND has been issued.
     */
    @NonNull
    public static Observable<Intent> bluetoothDeviceFound(final Context context) {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        return RxBroadcastReceiver.create(context, intentFilter)
                .map(RxBroadcastReceiver.getIntent());
    }

    /**
     * Create {@link Observable} which will produce a stream
     * when BluetoothAdapter.ACTION_DISCOVERY_FINISHED has been issued.
     */
    @NonNull
    public static Observable<Intent> bluetoothDiscoveryFinished(final Context context) {
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        return RxBroadcastReceiver.create(context, intentFilter)
                .map(RxBroadcastReceiver.getIntent());

    }
}
