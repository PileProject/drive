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

package com.pileproject.drive.programming.visual.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pileproject.drive.R;

public class ProgramListFragment extends DialogFragment {
    private final static String KEY_PROGRAMS = "sample_programs";
    private final static String KEY_IS_SAMPLE = "is_sample";

    private CharSequence[] mItems;
    private boolean mIsSample;

    public ProgramListFragment() {
        super();
    }

    public static ProgramListFragment newInstance(CharSequence[] items, boolean isSample) {
        Bundle bundle = new Bundle();
        bundle.putCharSequenceArray(KEY_PROGRAMS, items);
        bundle.putBoolean(KEY_IS_SAMPLE, isSample);

        ProgramListFragment programListFragment = new ProgramListFragment();
        programListFragment.setArguments(bundle);

        return programListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        mItems = bundle.getCharSequenceArray(KEY_PROGRAMS);
        mIsSample = bundle.getBoolean(KEY_IS_SAMPLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.programming_loadProgram);
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Return selected program name
                ProgrammingActivityBase activity = (ProgrammingActivityBase) getActivity();
                activity.onFinishSelectDialog(mItems[which].toString(), mIsSample);
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(
                                              DialogInterface dialog, int which) {
                                          dismiss();
                                      }
                                  });

        return builder.create();
    }
}
