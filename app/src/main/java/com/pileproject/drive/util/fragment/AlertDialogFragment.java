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
package com.pileproject.drive.util.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertDialogFragment.EventListener} interface
 * to handle interaction events.
 * Use {@link AlertDialogFragment.Builder} class to
 * show a dialog.
 */
public class AlertDialogFragment extends DialogFragment {
    private static final String KEY_TITLE          = "title";
    private static final String KEY_MESSAGE        = "message";
    private static final String KEY_POSITIVE_LABEL = "positive_label";
    private static final String KEY_NEGATIVE_LABEL = "negative_label";
    private static final String KEY_ITEMS          = "items";
    private static final String KEY_PARAMS         = "params";
    private static final String KEY_REQUEST_CODE   = "request_code";
    private static final String KEY_CANCELABLE     = "cancelable";
    private static final String KEY_GRAVITY        = "gravity";

    /**
     * A builder class for {@link AlertDialogFragment}.
     * Using this class enables to create an {@link AlertDialogFragment} instance by method chains
     * like {@link AlertDialog}.
     */
    public static class Builder {
        private static final int DEFAULT_REQUEST_CODE = -1;
        private static final String DEFAULT_TAG = "default_tag";

        final AppCompatActivity mActivity;

        final Fragment mParentFragment;

        int mRequestCode = DEFAULT_REQUEST_CODE;

        String mTag = DEFAULT_TAG;

        String mTitle;

        String mMessage;

        String mPositiveLabel;

        String mNegativeLabel;

        boolean mCancelable = false;

        String[] mItems;

        Bundle mParams;

        int mGravity = Gravity.NO_GRAVITY;

        boolean mAllowingStateLoss;

        /**
         * A constructor of the builder for {@link AppCompatActivity}.
         *
         * @param <A> a class of an activity that extends {@link AppCompatActivity} and {@link EventListener}
         * @param activity an activity that is trying to show a dialog
         */
        public <A extends AppCompatActivity & EventListener> Builder(@NonNull A activity) {
            mActivity = activity;
            mParentFragment = null;
        }

        /**
         * A constructor of the builder for {@link Fragment}.
         *
         * @param <F> a class of a fragment that extends {@link AppCompatActivity} and {@link EventListener}
         * @param parentFragment a fragment that is trying to show a dialog
         */
        public <F extends Fragment & EventListener> Builder(@NonNull F parentFragment) {
            mActivity = null;
            mParentFragment = parentFragment;
        }

        /**
         * Sets a tag for fragment.
         *
         * @param tag a tag to specify the objective of the dialog
         * @return the reference of this (for method chain)
         */
        public Builder setTag(@NonNull String tag) {
            mTag = tag;
            return this;
        }

        /**
         * Sets title string (String class).
         *
         * @param title the title string of the dialog
         * @return the reference of this (for method chain)
         */
        public Builder setTitle(@NonNull String title) {
            mTitle = title;
            return this;
        }

        /**
         * Sets title string (Android res).
         *
         * @param title the resource id of the title string of the dialog
         * @return the reference of this (for method chain)
         */
        public Builder setTitle(@StringRes int title) {
            return setTitle(getContext().getString(title));
        }

        /**
         * Sets message string (String class).
         *
         * @param message the message string of the dialog
         * @return the reference of this (for method chain)
         */
        public Builder setMessage(@NonNull String message) {
            mMessage = message;
            return this;
        }

        /**
         * Sets message string (Android res).
         *
         * @param message the resource id of the message string of the dialog
         * @return the reference of this (for method chain)
         */
        public Builder setMessage(@StringRes int message) {
            return setMessage(getContext().getString(message));
        }

        /**
         * Sets positive button label (String class).
         *
         * @param positiveLabel the label string of the positive button
         * @return the reference of this (for method chain)
         */
        public Builder setPositiveButtonLabel(@NonNull String positiveLabel) {
            mPositiveLabel = positiveLabel;
            return this;
        }

        /**
         * Sets positive button label (Android res).
         *
         * @param positiveLabel the resource id of the label string of the positive button
         * @return the reference of this (for method chain)
         */
        public Builder setPositiveButtonLabel(@StringRes int positiveLabel) {
            return setPositiveButtonLabel(getContext().getString(positiveLabel));
        }

        /**
         * Sets negative button label (String class).
         *
         * @param negativeLabel the label string of the negative button
         * @return the reference of this (for method chain)
         */
        public Builder setNegativeButtonLabel(@NonNull String negativeLabel) {
            mNegativeLabel = negativeLabel;
            return this;
        }

        /**
         * Sets negative button label (Android res).
         *
         * @param negativeLabel the resource id of the label string of the negative button
         * @return the reference of this (for method chain)
         */
        public Builder setNegativeButtonLabel(@NonNull int negativeLabel) {
            return setNegativeButtonLabel(getContext().getString(negativeLabel));
        }

        /**
         * Sets parameters for {@link AlertDialogFragment.EventListener#onDialogEventHandled(int, DialogInterface, int, Bundle)}.
         * This parameters will be passed to the function.
         *
         * @param params parameters for the function
         * @return the reference of this (for method chain)
         */
        public Builder setParams(@NonNull Bundle params) {
            mParams = new Bundle(params);
            return this;
        }

        /**
         * Sets string items displayed in a list view.
         *
         * @param items a set of item string
         * @return the reference of this (for method chain)
         */
        public Builder setItems(@NonNull String... items) {
            mItems = items;
            return this;
        }

        /**
         * Specifies the instance can be canceled or not
         * (i.e., can be dismissed without tapping buttons).
         *
         * @param cancelable (<code>true</code>) or not (<code>false</code>)
         * @return the reference of this (for method chain)
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * Specifies the transaction is allowed to lose state.
         *
         * @param allowingStateLoss the fragment will be showed by {@link FragmentTransaction#commitAllowingStateLoss()}
         *                       if true, otherwise showed by {@link FragmentTransaction#commit}.
         * @return the reference of this (for method chain)
         */
        public Builder setAllowingStateLoss(boolean allowingStateLoss) {
            mAllowingStateLoss = allowingStateLoss;
            return this;
        }

        /**
         * Sets gravity of the instance.
         *
         * @param gravity a {@link Gravity} variable
         * @return the reference of this (for method chain)
         */
        public Builder setWindowGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        /**
         * Sets request code.
         * This value will be passed to {@link AlertDialogFragment.EventListener#onDialogEventHandled(int, DialogInterface, int, Bundle)}
         * to distinguish callers.
         *
         * @param requestCode the request code to distinguish a caller
         * @return the reference of this (for method chain)
         */
        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        /**
         * Shows the instance with configuration you specified before this function call.
         */
        public void show() {
            Bundle args = new Bundle();

            args.putString(KEY_TITLE, mTitle);
            args.putString(KEY_MESSAGE, mMessage);
            args.putString(KEY_POSITIVE_LABEL, mPositiveLabel);
            args.putString(KEY_NEGATIVE_LABEL, mNegativeLabel);
            args.putStringArray(KEY_ITEMS, mItems);

            args.putBoolean(KEY_CANCELABLE, mCancelable);

            args.putInt(KEY_GRAVITY, mGravity);

            if (mParams != null) {
                args.putBundle(KEY_PARAMS, mParams);
            }

            final AlertDialogFragment f = new AlertDialogFragment();

            if (mParentFragment != null) {
                f.setTargetFragment(mParentFragment, mRequestCode);
            }
            else {
                args.putInt(KEY_REQUEST_CODE, mRequestCode);
            }

            f.setArguments(args);

            FragmentManager fragmentManager  = (mParentFragment != null)
                    ? mParentFragment.getChildFragmentManager()
                    : mActivity.getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(f, mTag);

            if (mAllowingStateLoss) {
                fragmentTransaction.commitAllowingStateLoss();
            }
            else {
                fragmentTransaction.commit();
            }
        }

        private Context getContext() {
            return (mActivity == null ? mParentFragment.getActivity() : mActivity).getApplicationContext();
        }
    }

    private EventListener mListener;

    public AlertDialogFragment() {
        // required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment parentFragment = getParentFragment();

        try {
            mListener = (parentFragment == null) ?
                    (EventListener) activity : (EventListener) parentFragment;
        }
        catch (ClassCastException e) {
            throw new IllegalStateException(
                    "Activities or Fragments that contain AlertDialogFragment " +
                    "should implement AlertDialogFragment.EventListener " +
                    "to handle events from AlertDialogFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                mListener.onDialogEventHandled(getRequestCode(), dialog, which, getArguments().getBundle(KEY_PARAMS));
            }
        };

        Bundle args = getArguments();

        setCancelable(args.getBoolean(KEY_CANCELABLE));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String title = args.getString(KEY_TITLE);
        String message = args.getString(KEY_MESSAGE);
        String positiveLabel = args.getString(KEY_POSITIVE_LABEL);
        String negativeLabel = args.getString(KEY_NEGATIVE_LABEL);

        // set strings if not empty
        if (! TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (! TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (! TextUtils.isEmpty(positiveLabel)) {
            builder.setPositiveButton(positiveLabel, listener);
        }
        if (! TextUtils.isEmpty(negativeLabel)) {
            builder.setNegativeButton(negativeLabel, listener);
        }

        // if there is a list, set it as list view
        String[] items = args.getStringArray(KEY_ITEMS);

        if (items != null && items.length > 0) {
            builder.setItems(items, listener);
        }

        AlertDialog dialog = builder.create();

        dialog.getWindow().setGravity(args.getInt(KEY_GRAVITY));

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onDialogEventCancelled(getRequestCode(), dialog, getArguments().getBundle(KEY_PARAMS));
    }

    private int getRequestCode() {
        return getArguments().containsKey(KEY_REQUEST_CODE) ?
                getArguments().getInt(KEY_REQUEST_CODE) : getTargetRequestCode();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface EventListener {
        /**
         * Called when a button (positive, negative or neutral) is clicked.
         *
         * @param requestCode the request code that has been set by {@link AlertDialogFragment.Builder#setRequestCode}
         * @param dialog the dialog that was showed
         * @param which the pushed button
         * @param params the parameters that have been set by {@link AlertDialogFragment.Builder#setParams}
         */
        void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params);

        /**
         * Called when this dialog is cancelled (e.g. user tapped outside of the dialog).
         *
         * @param requestCode the request code that has been set by {@link AlertDialogFragment.Builder#setRequestCode}
         * @param dialog the dialog that was showed
         * @param params the parameters that have been set by {@link AlertDialogFragment.Builder#setParams}
         */
        void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params);
    }
}
