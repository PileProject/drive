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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;

import com.pileproject.drive.R;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertDialogFragment.EventListener} interface
 * to handle interaction events.
 * Use {@link AlertDialogFragment.Builder} class to
 * show a dialog.
 */
public class AlertDialogFragment extends DialogFragment {
    /**
     * Builder class for {@link AlertDialogFragment}
     * Using this class enables to create an AlertDialogFragment instance by method chains
     * like {@link AlertDialog}
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

        /**
         * Constructor of builder for {@link AppCompatActivity}
         * @param activity
         * @param <A>
         */
        public <A extends AppCompatActivity & EventListener> Builder(@NonNull A activity) {
            mActivity = activity;
            mParentFragment = null;
        }

        /**
         * Constructor of builder for {@link Fragment}
         * @param parentFragment
         * @param <F>
         */
        public <F extends Fragment & EventListener> Builder(@NonNull F parentFragment) {
            mActivity = null;
            mParentFragment = parentFragment;
        }

        /**
         * set tag for the fragment
         * @param tag
         * @return reference of this (for method chain)
         */
        public Builder setTag(@NonNull String tag) {
            mTag = tag;
            return this;
        }

        /**
         * set title string (String class)
         * @param title
         * @return reference of this (for method chain)
         */
        public Builder setTitle(@NonNull String title) {
            mTitle = title;
            return this;
        }

        /**
         * set title string (Android res)
         * @param title
         * @return reference of this (for method chain)
         */
        public Builder setTitle(@StringRes int title) {
            return setTitle(getContext().getString(title));
        }

        /**
         * set message string (String class)
         * @param message
         * @return reference of this (for method chain)
         */
        public Builder setMessage(@NonNull String message) {
            mMessage = message;
            return this;
        }

        /**
         * set message string (Android res)
         * @param message
         * @return reference of this (for method chain)
         */
        public Builder setMessage(@StringRes int message) {
            return setMessage(getContext().getString(message));
        }

        /**
         * set positive button label (String class)
         * @param positiveLabel
         * @return reference of this (for method chain)
         */
        public Builder setPositiveButtonLabel(@NonNull String positiveLabel) {
            mPositiveLabel = positiveLabel;
            return this;
        }

        /**
         * set positive button label (Android res)
         * @param positiveLabel
         * @return reference of this (for method chain)
         */
        public Builder setPositiveButtonLabel(@StringRes int positiveLabel) {
            return setPositiveButtonLabel(getContext().getString(positiveLabel));
        }

        /**
         * set positive button label (String class)
         * @param negativeLabel
         * @return reference of this (for method chain)
         */
        public Builder setNegativeButtonLabel(@NonNull String negativeLabel) {
            mNegativeLabel = negativeLabel;
            return this;
        }

        /**
         * set positive button label (Android res)
         * @param negativeLabel
         * @return reference of this (for method chain)
         */
        public Builder setNegativeButtonLabel(@NonNull int negativeLabel) {
            return setNegativeButtonLabel(getContext().getString(negativeLabel));
        }

        /**
         * set params for {@link AlertDialogFragment.EventListener#onDialogEventHandled(int, DialogInterface, int, Bundle)}
         * this params will be passed to the function
         * @param params
         * @return reference of this (for method chain)
         */
        public Builder setParams(@NonNull Bundle params) {
            mParams = new Bundle(params);
            return this;
        }

        /**
         * set string items displayed in a list view
         * @param items
         * @return reference of this (for method chain)
         */
        public Builder setItems(@NonNull String... items) {
            mItems = items;
            return this;
        }

        /**
         * specify the instance can be cancelable or not
         * (i.e., can be dismissed without tapping buttons)
         * @param cancelable
         * @return reference of this (for method chain)
         */
        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * set gravity of the instance
         * @param gravity {@link Gravity} variable
         * @return reference of this (for method chain)
         */
        public Builder setWindowGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        /**
         * set request code
         * this value will be passed to {@link AlertDialogFragment.EventListener#onDialogEventHandled(int, DialogInterface, int, Bundle)}
         * to distinguish callers
         * @param requestCode
         * @return reference of this (for method chain)
         */
        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        /**
         * show the instance with configuration you specified before function call
         */
        public void show() {
            Bundle args = new Bundle();

            Context context = getContext();
            args.putString(context.getString(R.string.key_fragment_title), mTitle);
            args.putString(context.getString(R.string.key_fragment_message), mMessage);
            args.putString(context.getString(R.string.key_fragment_positiveLabel), mPositiveLabel);
            args.putString(context.getString(R.string.key_fragment_negativeLable), mNegativeLabel);
            args.putStringArray(context.getString(R.string.key_fragment_items), mItems);

            args.putBoolean(context.getString(R.string.key_fragment_cancelable), mCancelable);

            args.putInt(context.getString(R.string.key_fragment_gravity), mGravity);

            if (mParams != null) {
                args.putBundle(context.getString(R.string.key_fragment_params), mParams);
            }

            final AlertDialogFragment f = new AlertDialogFragment();

            if (mParentFragment != null) {
                f.setTargetFragment(mParentFragment, mRequestCode);
            }
            else {
                args.putInt(context.getString(R.string.key_fragment_requestCode), mRequestCode);
            }

            f.setArguments(args);

            // show instance according to the parent instance
            if (mParentFragment != null) {
                f.show(mParentFragment.getChildFragmentManager(), mTag);
            }
            else {
                f.show(mActivity.getSupportFragmentManager(), mTag);
            }
        }

        private Context getContext() {
            return (mActivity == null ? mParentFragment.getActivity() : mActivity).getApplicationContext();
        }
    }

    private EventListener mListener;

    public AlertDialogFragment() {
        // Required empty public constructor
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
                mListener.onDialogEventHandled(getRequestCode(), dialog, which,
                                               getArguments().getBundle(getString(R.string.key_fragment_params)));
            }
        };

        Bundle args = getArguments();

        setCancelable(args.getBoolean(getString(R.string.key_fragment_cancelable)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String title = args.getString(getString(R.string.key_fragment_title));
        String message = args.getString(getString(R.string.key_fragment_message));
        String positiveLabel = args.getString(getString(R.string.key_fragment_positiveLabel));
        String negativeLabel = args.getString(getString(R.string.key_fragment_negativeLable));

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
        String[] items = args.getStringArray(getString(R.string.key_fragment_items));

        if (items != null && items.length > 0) {
            builder.setItems(items, listener);
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(args.getInt(getString(R.string.key_fragment_gravity)));
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onDialogEventCancelled(getRequestCode(), dialog,
                                         getArguments().getBundle(getString(R.string.key_fragment_params)));
    }

    private int getRequestCode() {
        final String keyRequestCode = getString(R.string.key_fragment_requestCode);
        return getArguments().containsKey(keyRequestCode) ?
                getArguments().getInt(keyRequestCode) : getTargetRequestCode();
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
         * Called when a button (positive, negative or neutral) is clicked
         * @param requestCode request code that has been set by {@link AlertDialogFragment.Builder#setRequestCode}
         * @param dialog
         * @param which
         */
        void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params);

        /**
         * Called when this dialog is cancelled (e.g. user tapped outside of the dialog)
         * @param requestCode
         */
        void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params);
    }
}
