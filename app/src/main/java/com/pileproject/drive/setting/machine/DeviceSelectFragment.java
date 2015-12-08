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

package com.pileproject.drive.setting.machine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pileproject.drive.R;
import com.pileproject.drive.util.SharedPreferencesWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Fragment for selecting device
 *
 * @author yusaku
 * @version 1.0 11-Oct-2013
 */
public class DeviceSelectFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String DEVICE_ADDRESS_PREFIX = "00:16:53";

    private static final int REQUEST_ENABLE_BT = 1;

    private Activity mActivity;
    private BluetoothAdapter mBtAdapter = null;

    private Button mDiscoverButton = null;
    private Button mBackButton = null;

    // fields for new device list
    private ListView mNewDevicesListView = null;
    private DeviceListAdapter mNewDevicesAdapter = null;
    private LinkedList<BluetoothDevice> mNewDevices = null;

    // fields for bonded device list
    private ListView mBondedDevicesListView = null;
    private DeviceListAdapter mBondedDevicesAdapter = null;
    private LinkedList<BluetoothDevice> mBondedDevices = null;

    private ProgressDialogFragment mProgressDialogFragment = null;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get a BluetoothDevice object from the Intent
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (/*( device.getAddress().startsWith
                    (DEVICE_ADDRESS_PREFIX) || DeployUtils.isDebugMode
                    (mActivity.getApplicationContext())) && */
                            mNewDevices.indexOf(device) == -1) {
                        mNewDevices.add(device);
                    }
                }
            }

            // When discovery is finished, change the Activity title
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals
                    (action)) {
                finalizeDiscovery();
            }
        }
    };
    private ProgressDialogFragment mBtConnectionProgressDialogFragment = null;
    private BluetoothTask mBluetoothTask = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_deviceselect,
                                  container,
                                  false);

        mDiscoverButton =
                (Button) v.findViewById(R.id.deviceselect_discoverButton);
        mBackButton = (Button) v.findViewById(R.id.deviceselect_backButton);
        mBondedDevicesListView =
                (ListView) v.findViewById(R.id.deviceselect_bondedDeviceList);
        mBondedDevicesListView.setEmptyView((View) v.findViewById(R.id.deviceselect_emptyBondedDeviceList));
        mNewDevicesListView =
                (ListView) v.findViewById(R.id.deviceselect_newDeviceList);
        mNewDevicesListView.setEmptyView((View) v.findViewById(R.id.deviceselect_emptyNewDeviceList));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        mDiscoverButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverBluetoothDevices();
            }
        });

        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        mBondedDevices = new LinkedList<BluetoothDevice>();
        mBondedDevicesAdapter = new DeviceListAdapter(mActivity,
                                                      R.layout.view_devicelist,
                                                      mBondedDevices);
        mBondedDevicesListView.setAdapter(mBondedDevicesAdapter);
        mBondedDevicesListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mBondedDevicesListView.setOnItemClickListener(new OnItemClickListener
                () {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                BluetoothDevice btDev =
                        (BluetoothDevice) listView.getItemAtPosition(position);
                SharedPreferencesWrapper.saveDefaultDeviceAddress(mActivity,
                                                                  btDev.getAddress());
                Toast.makeText(mActivity,
                               getString(R.string.deviceselect_toast_setDefault) +
                                       "\n" + btDev.getName(),
                               Toast.LENGTH_LONG).show();
            }
        });

        mNewDevices = new LinkedList<BluetoothDevice>();
        mNewDevicesAdapter = new DeviceListAdapter(mActivity,
                                                   R.layout.view_devicelistitem,
                                                   mNewDevices);
        mNewDevicesListView.setAdapter(mNewDevicesAdapter);
        mNewDevicesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                mBluetoothTask = new BluetoothTask();
                mBluetoothTask.execute((BluetoothDevice) listView
                        .getItemAtPosition(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mDiscoverButton.setEnabled(true);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // check whether bluetooth function is available or not
        if (mBtAdapter == null) {
            ConfirmationDialogFragment btDiag =
                    ConfirmationDialogFragment.newInstance(getString(R.string.deviceselect_needBt_title),
                                                           getString(R.string.deviceselect_needBt_message));

            btDiag.setOnClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // turn discover button unable
                    mDiscoverButton.setEnabled(false);
                }
            });

            btDiag.show(getFragmentManager(), "dialog");
        } else {
            getBondedDevices();
        }
        mActivity.registerReceiver(mReceiver,
                                   new IntentFilter(BluetoothDevice
                                                            .ACTION_FOUND));
        mActivity.registerReceiver(mReceiver,
                                   new IntentFilter(BluetoothAdapter
                                                            .ACTION_DISCOVERY_FINISHED));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            finalizeDiscovery();
        }

        mActivity.unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    discoverBluetoothDevices();
                } else {
                    ConfirmationDialogFragment cfrmDiag =
                            ConfirmationDialogFragment.newInstance(getString
                                                                           (R.string.deviceselect_enableBt_title),
                                                                   getString
                                                                           (R.string.deviceselect_enableBt_message));
                    cfrmDiag.show(getFragmentManager(), "tag");
                }
        }
    }

    private void sieveDevices(Collection<BluetoothDevice> devices) {
        if (devices.size() == 0) {
            return;
        }

        //		for (Iterator<BluetoothDevice> itr = devices.iterator(); itr
        // .hasNext();) {
        //			BluetoothDevice btDev = itr.next();
        //
        //			if (!btDev.getAddress().startsWith(DEVICE_ADDRESS_PREFIX)
        // && DeployUtils.isReleaseMode(mActivity.getApplicationContext())) {
        //				itr.remove();
        //			}
        //		}
    }

    private int findIndexOfDevice(
            Collection<BluetoothDevice> devices, String address) {
        if (devices.size() == 0) {
            return -1;
        }

        int index = 0;
        for (Iterator<BluetoothDevice> itr = devices.iterator();
             itr.hasNext(); ) {
            BluetoothDevice btdev = itr.next();

            if (btdev.getAddress().equals(address)) {
                return index;
            }

            ++index;
        }

        return -1;
    }

    /**
     * get bonded bluetooth devices and display them on ListView
     * <p/>
     * if a default device is already registered in preference,
     * this function also indicates the device as checked item
     */
    private void getBondedDevices() {
        mBondedDevices.clear();
        mBondedDevices.addAll(mBtAdapter.getBondedDevices());
        sieveDevices(mBondedDevices);
        mBondedDevicesAdapter.notifyDataSetChanged();

        String defaultDevAddr =
                SharedPreferencesWrapper.loadDefaultDeviceAddress(mActivity);
        int defaultDevIdx = findIndexOfDevice(mBondedDevices, defaultDevAddr);
        mBondedDevicesListView.setItemChecked(defaultDevIdx, true);
    }

    private void discoverBluetoothDevices() {
        // if the bluetooth function is not enabled, let the user enable it
        if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter
                                                      .ACTION_REQUEST_ENABLE),
                                   REQUEST_ENABLE_BT);
            return;
        }

        mProgressDialogFragment =
                ProgressDialogFragment.newInstance(getString(R.string.deviceselect_progress_scanning_title),
                                                   getString(R.string.deviceselect_progress_scanning_message));
        mProgressDialogFragment.setCancelable(false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(mProgressDialogFragment, null);
        ft.commitAllowingStateLoss();

        mActivity.findViewById(R.id.deviceselect_newDeviceLayout)
                .setVisibility(View.VISIBLE);
        mActivity.setTitle(R.string.deviceselect_scanning);
        mActivity.setProgressBarIndeterminate(true);

        mDiscoverButton.setEnabled(false);

        mBtAdapter.startDiscovery();
    }

    private void finalizeDiscovery() {
        mDiscoverButton.setEnabled(true);

        mActivity.setProgressBarIndeterminate(false);
        mActivity.setTitle(R.string.deviceselect_selectDevice);

        mNewDevicesAdapter.notifyDataSetChanged();
        mProgressDialogFragment.dismissAllowingStateLoss();
    }

    /**
     * A dialog which appears when let users confirm information
     */
    public static class ConfirmationDialogFragment extends DialogFragment {
        private DialogInterface.OnClickListener listener = null;

        public static ConfirmationDialogFragment newInstance(
                String title, String msg) {
            ConfirmationDialogFragment cfrmDiag =
                    new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("msg", msg);
            cfrmDiag.setArguments(args);

            return cfrmDiag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");
            String msg = getArguments().getString("msg");

            Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(getString(R.string.ok), listener);
            AlertDialog dialog = builder.create();
            return dialog;
        }

        public void setOnClickListener(
                DialogInterface.OnClickListener listener) {
            this.listener = listener;
        }
    }

    /**
     * A dialog which appears during the user is scanning device
     */
    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance(
                String title, String message) {
            ProgressDialogFragment prgDiag = new ProgressDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            prgDiag.setArguments(args);

            return prgDiag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = getArguments().getString("title");
            String message = getArguments().getString("message");

            ProgressDialog pDiag = new ProgressDialog(getActivity());
            pDiag.setTitle(title);
            pDiag.setMessage(message);
            pDiag.setCanceledOnTouchOutside(false);
            pDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            return pDiag;
        }
    }

    class BluetoothTask extends AsyncTask<BluetoothDevice, Void, Boolean> {
        private static final String BT_TAG = "BluetoothTask";

        private final UUID SPP_UUID =
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        private BluetoothDevice device;

        @Override
        protected void onPreExecute() {
            mBtConnectionProgressDialogFragment =
                    ProgressDialogFragment.newInstance(getString(R.string.deviceselect_progress_connecting_title),
                                                       getString(R.string.deviceselect_progress_connecting_message));
            mBtConnectionProgressDialogFragment.setCancelable(false);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(mBtConnectionProgressDialogFragment, null);
            ft.commitAllowingStateLoss();
        }

        @Override
        protected Boolean doInBackground(BluetoothDevice... devices) {
            device = devices[0];

            BluetoothSocket socket = null;

            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
            }

            try {
                socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();
                socket.close();
            } catch (IOException e) {
                Log.d(BT_TAG, e.getMessage());

                try {
                    // try another method
                    Method method = device.getClass()
                            .getMethod("createRfcommSocket",
                                       new Class[]{int.class});
                    socket = (BluetoothSocket) method.invoke(device,
                                                             Integer.valueOf
                                                                     (1));
                    socket.connect();
                    socket.close();
                } catch (IOException e2) {
                    Log.d(BT_TAG, e2.getMessage());
                    return false;
                }
                // method invocation failed
                catch (Exception e2) {
                    Log.d(BT_TAG, e2.getMessage());
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mBtConnectionProgressDialogFragment.dismissAllowingStateLoss();

            if (result) {
                if (mBondedDevicesAdapter.getPosition(device) == -1) {
                    mBondedDevicesAdapter.add(device);
                    mBondedDevicesListView.setItemChecked
                            (mBondedDevicesAdapter.getPosition(
                            device), true);
                }

                mNewDevicesAdapter.remove(device);

                SharedPreferencesWrapper.saveDefaultDeviceAddress(mActivity,
                                                                  device.getAddress());
                Toast.makeText(mActivity,
                               getString(R.string.deviceselect_toast_setDefault) +
                                       "\n" + device.getName(),
                               Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity,
                               getString(R.string.deviceselect_toast_cannotConnect) +
                                       "\n" + device.getName(),
                               Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mBtConnectionProgressDialogFragment.dismissAllowingStateLoss();
        }
    }
}
