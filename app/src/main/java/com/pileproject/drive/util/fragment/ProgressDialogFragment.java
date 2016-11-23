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
package com.pileproject.drive.util.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

/**
 * A simple dialog with a progress bar (intermediate style).
 * With simply calling {@link ProgressDialogFragment#showDialog(FragmentManager, String, String, String)},
 * a ProgressDialog will appear.
 * When no longer needed, call {@link ProgressDialogFragment#dismissDialog()}.
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String KEY_TITLE          = "title";
    private static final String KEY_MESSAGE        = "message";
    private static ProgressDialog sProgressDialog;

    public ProgressDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Show a progress dialog with given parameters.
     * The dialog is based on {@link DialogFragment}.
     * @param manager {@link FragmentManager} or its sub-class.
     * @param title dialog title
     * @param message dialog message
     * @param tag fragment tag
     */
    public static void showDialog(FragmentManager manager, String title, String message, String tag) {
        ProgressDialogFragment f = newInstance(title, message);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(f, tag);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Show a progress dialog with given parameters.
     * You can specify title and messages with resource ID.
     * @param context Context for retrieving strings
     * @param manager {@link FragmentManager} or its sub-class.
     * @param titleRes dialog title
     * @param messageRes dialog message
     * @param tag fragment tag
     */
    public static void showDialog(Context context, FragmentManager manager, @StringRes int titleRes, @StringRes int messageRes, String tag) {
        showDialog(manager, context.getString(titleRes), context.getString(messageRes), tag);
    }

    /**
     * Dismiss the dialog you opened.
     * This function is idempotent (i.e., there's no effect by calling twice or more in a certain state)
     */
    public static void dismissDialog() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
        }
    }

    /**
     * Simple factory method for internal use
     */
    private static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (sProgressDialog != null) {
            return sProgressDialog;
        }

        sProgressDialog = new ProgressDialog(getActivity());

        Bundle args = getArguments();

        String title = args.getString(KEY_TITLE);
        String message = args.getString(KEY_MESSAGE);

        if (! TextUtils.isEmpty(title)) {
            sProgressDialog.setTitle(title);
        }
        if (! TextUtils.isEmpty(message)) {
            sProgressDialog.setMessage(message);
        }

        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return sProgressDialog;
    }

    @Override
    public Dialog getDialog() {
        return sProgressDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sProgressDialog = null;
    }

}
