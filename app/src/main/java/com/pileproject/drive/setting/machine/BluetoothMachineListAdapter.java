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
package com.pileproject.drive.setting.machine;

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
 * ListView adapter class for custom view
 *
 * @author yusaku
 * @version 1.0 4-June-2013
 */
public class BluetoothMachineListAdapter extends ArrayAdapter<BluetoothDevice> {

    private Context mContext = null;
    private int mTextViewResourceId = 0;
    private List<BluetoothDevice> mDevices;
    private LayoutInflater mInflater;

    public BluetoothMachineListAdapter(
            Context context, int textViewResourceId, List<BluetoothDevice> devices) {
        super(context, textViewResourceId, devices);

        mContext = context;
        mTextViewResourceId = textViewResourceId;
        mDevices = devices;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public boolean contains(BluetoothDevice device) {
        return mDevices.contains(device);
    }

}