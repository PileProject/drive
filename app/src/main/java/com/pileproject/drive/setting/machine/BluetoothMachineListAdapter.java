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
package com.pileproject.drive.setting.machine;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pileproject.drive.R;

import java.util.List;

/**
 * A list view adapter class for {@link BluetoothDevice}.
 */
public class BluetoothMachineListAdapter extends ArrayAdapter<BluetoothDevice> {

    private int mTextViewResourceId = 0;
    private List<BluetoothDevice> mDevices;
    private LayoutInflater mInflater;

    /**
     * @param context the context of the {@link Activity} that uses this adapter
     * @param textViewResourceId the resource id of {@link TextView} of the list
     * @param devices the list of Bluetooth devices
     */
    public BluetoothMachineListAdapter(Context context, int textViewResourceId, List<BluetoothDevice> devices) {
        super(context, textViewResourceId, devices);

        mTextViewResourceId = textViewResourceId;
        mDevices = devices;

        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(mTextViewResourceId, parent, false);
        }

        BluetoothDevice device = mDevices.get(position);

        ((TextView) view.findViewById(R.id.deviceListItemView_deviceNameText)).setText(device.getName());
        ((TextView) view.findViewById(R.id.deviceListItemView_deviceAddressText)).setText(device.getAddress());

        return view;
    }

    /**
     * Checks if the list contains the device or not.
     *
     * @param device the target device
     * @return contains (<code>true</code>) or not (<code>false</code>)
     */
    public boolean contains(BluetoothDevice device) {
        return mDevices.contains(device);
    }

}