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
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.util.bluetooth.BluetoothUtil;
import com.pileproject.drive.util.bluetooth.RxBluetoothConnector;
import com.pileproject.drive.util.broadcast.RxBluetoothBroadcastReceiver;
import com.pileproject.drive.util.fragment.AlertDialogFragment;
import com.pileproject.drive.util.fragment.ProgressDialogFragment;

import java.util.LinkedList;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A fragment for selecting the default Bluetooth device. This fragment will be used by
 * {@link BluetoothMachineSelectPreference}.
 */
public class BluetoothMachineSelectFragment extends DialogFragment implements AlertDialogFragment.EventListener {

    private static final int REQUEST_CODE_ENABLE_BT = 1;

    private static final int DIALOG_REQUEST_ENABLE_BT = 2;
    private static final int DIALOG_REQUEST_NO_BLUETOOTH = 3;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    // a field for a new device list
    private ListView mNewDevicesListView;

    // a field for a bonded device list
    private ListView mPairedDevicesListView;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_bluetoothMachineSelect);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth_machine_select, container, false);

        mPairedDevicesListView = (ListView) v.findViewById(R.id.machineSelect_listPaired);
        mPairedDevicesListView.setEmptyView(v.findViewById(R.id.machineSelect_noPairedText));
        mNewDevicesListView = (ListView) v.findViewById(R.id.machineSelect_listNew);
        mNewDevicesListView.setEmptyView(v.findViewById(R.id.machineSelect_notFoundText));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        resizeDialog();

        mPairedDevicesListView.setAdapter(new BluetoothMachineListAdapter(getActivity(),
                                                                          R.layout.view_bluetooth_machine_list,
                                                                          new LinkedList<BluetoothDevice>()));
        mPairedDevicesListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mPairedDevicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                BluetoothDevice bluetoothDevice = (BluetoothDevice) listView.getItemAtPosition(position);

                MachinePreferences.get(getActivity()).setMacAddress(bluetoothDevice.getAddress());

                Toast.makeText(getActivity(),
                               getString(R.string.setting_bluetoothMachineSelect_toast_setDefault) + "\n"
                                       + bluetoothDevice.getName(),
                               Toast.LENGTH_LONG).show();
            }
        });


        mNewDevicesListView.setAdapter(new BluetoothMachineListAdapter(getActivity(),
                                                                       R.layout.view_bluetooth_machine_list,
                                                                       new LinkedList<BluetoothDevice>()));
        mNewDevicesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                BluetoothDevice bluetoothDevice = (BluetoothDevice) listView.getItemAtPosition(position);

                final String bluetoothDeviceName = bluetoothDevice.getName();

                ProgressDialogFragment.showDialog(getActivity(), getChildFragmentManager(),
                        R.string.setting_bluetoothMachineSelect_progress_connecting_title,
                        R.string.setting_bluetoothMachineSelect_progress_connecting_message, "tag");

                // cancel scanning process because this is very heavyweight
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                mSubscriptions.add(
                        RxBluetoothConnector.pairing(bluetoothDevice)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<BluetoothDevice>() {
                                    @Override
                                    public void onCompleted() {
                                        updatePairedList();

                                        BluetoothAdapter.getDefaultAdapter().startDiscovery();

                                        ProgressDialogFragment.dismissDialog();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        BluetoothAdapter.getDefaultAdapter().startDiscovery();

                                        Toast.makeText(getActivity(),
                                                getString(R.string.setting_bluetoothMachineSelect_toast_cannotConnect,
                                                          bluetoothDeviceName),
                                                Toast.LENGTH_LONG).show();

                                        ProgressDialogFragment.dismissDialog();
                                    }

                                    @Override
                                    public void onNext(BluetoothDevice bluetoothDevice) {
                                        MachinePreferences.get(getActivity()).setMacAddress(bluetoothDevice.getAddress());

                                        Toast.makeText(getActivity(),
                                                getString(R.string.setting_bluetoothMachineSelect_toast_setDefault,
                                                          bluetoothDeviceName),
                                                Toast.LENGTH_LONG).show();

                                        BluetoothMachineListAdapter adapter
                                                = (BluetoothMachineListAdapter) mNewDevicesListView.getAdapter();
                                        adapter.remove(bluetoothDevice);
                                    }
                                }));
            }
        });
    }

    /**
     * This function should be called in {@link DialogFragment#onActivityCreated(Bundle)}.
     * Otherwise, the dialog size will never be changed.
     */
    private void resizeDialog() {
        Dialog dialog = getDialog();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // resize window large enough to display list views
        int dialogWidth = (int) (metrics.widthPixels * 0.8);
        int dialogHeight = (int) (metrics.heightPixels * 0.6);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        lp.height = dialogHeight;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!BluetoothUtil.hasBluetoothFunction()) {
            new AlertDialogFragment.Builder(this)
                    .setTitle(R.string.setting_bluetoothMachineSelect_alert_noBt_title)
                    .setMessage(R.string.setting_bluetoothMachineSelect_alert_noBt_message)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .setRequestCode(DIALOG_REQUEST_NO_BLUETOOTH)
                    .setCancelable(false)
                    .show();
            return;
        }

        // TODO: handle duplicate callings
        // typically occurred in the following user interaction
        //   1. the user press ok button
        //      1.1 bluetooth permission window will be appeared
        //   2. the user make the app background (e.g., press home button)
        //   3. the user come back to this app
        //   -> the second bluetooth permission window will be appeared
        if (!BluetoothUtil.isBluetoothEnabled()) {
            new AlertDialogFragment.Builder(this)
                    .setRequestCode(DIALOG_REQUEST_ENABLE_BT)
                    .setTitle(R.string.setting_bluetoothMachineSelect_alert_enableBt_title)
                    .setMessage(R.string.setting_bluetoothMachineSelect_alert_enableBt_message)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .setNegativeButtonLabel(android.R.string.cancel)
                    .setCancelable(false)
                    .show();
            return;
        }

        updatePairedList();

        subscribeBluetoothDiscovery();

        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (BluetoothUtil.hasBluetoothFunction()) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }

        // onPause is called when the bluetooth pairing dialog appears
        // so mSubscription.unsubscribe should not be called in here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSubscriptions.unsubscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_BT: {
                if (resultCode != Activity.RESULT_OK) {
                    dismissAllowingStateLoss();
                }

                break;
            }
        }
    }

    private void subscribeBluetoothDiscovery() {
        mSubscriptions.add(
                RxBluetoothBroadcastReceiver.bluetoothDeviceFound(getActivity())
                        .subscribeOn(Schedulers.newThread())
                        // should run on UI thread (main thread) to change UI components
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Intent>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(Intent intent) {
                                BluetoothDevice bluetoothDevice
                                        = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                    return;
                                }

                                BluetoothMachineListAdapter adapter
                                        = (BluetoothMachineListAdapter) mNewDevicesListView.getAdapter();

                                if (adapter.contains(bluetoothDevice)) {
                                    return;
                                }

                                adapter.add(bluetoothDevice);
                                adapter.notifyDataSetChanged();
                            }
                        }));

        mSubscriptions.add(
                RxBluetoothBroadcastReceiver.bluetoothDiscoveryFinished(getActivity())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Observer<Intent>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Intent intent) {
                                // enable continuous discovering
                                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                            }
                        }));
    }

    private void updatePairedList() {
        BluetoothMachineListAdapter listAdapter = (BluetoothMachineListAdapter) mPairedDevicesListView.getAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listAdapter.clear();
        listAdapter.addAll(bluetoothAdapter.getBondedDevices());

        String macAddress = MachinePreferences.get(getActivity()).getMacAddress();

        if (BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

            if (listAdapter.contains(device)) {
                mPairedDevicesListView.setItemChecked(listAdapter.getPosition(device), true);
            }
        }
    }

    @Override
    public void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params) {
        switch (requestCode) {
            case DIALOG_REQUEST_ENABLE_BT: {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BT);
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dismissAllowingStateLoss();
                }

                break;
            }

            case DIALOG_REQUEST_NO_BLUETOOTH: {
                dismissAllowingStateLoss();
                break;
            }
        }
    }

    @Override
    public void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params) {

    }
}
