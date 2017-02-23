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
package com.pileproject.drive.setting.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.pileproject.drive.R;
import com.pileproject.drive.database.ProgramDataManager;
import com.pileproject.drive.util.fragment.AlertDialogFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment for showing the list of programs. This fragment will be used by {@link ProgramListPreference}.
 */
public class ProgramListFragment extends DialogFragment implements AlertDialogFragment.EventListener {
    private ProgramDataManager mManager;
    private Button mDeleteButton;

    // 0 : sample programs, 1 : user programs
    private static final int SAMPLE_PROGRAM = 0;
    private static final int USER_PROGRAM = 1;
    private static final int NUM_PROGRAM_KINDS = 2;

    private Button[] mProgramsCheckAllButton = new Button[NUM_PROGRAM_KINDS];
    private ListView[] mProgramListView = new ListView[NUM_PROGRAM_KINDS];
    private ProgramDataAdapter[] mProgramDataAdapter = new ProgramDataAdapter[2];
    private ArrayList<Map<String, Boolean>> mCheckedPrograms = new ArrayList<>();

    public ProgramListFragment() {
        // required empty constructor
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.setting_programList);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_program_list, container, false);

        // create ProgramDataManager to list programs
        mManager = ProgramDataManager.getInstance();

        mCheckedPrograms.add(new LinkedHashMap<String, Boolean>());
        mCheckedPrograms.add(new LinkedHashMap<String, Boolean>());

        // prepare data for Data adapter
        initializeProgramDataAdapter();

        // create ListView to show all programs
        // an item in ListView consists of (checkbox, program name) [CustomView]
        mProgramListView[0] = (ListView) v.findViewById(R.id.programList_sampleProgramListView);
        mProgramListView[1] = (ListView) v.findViewById(R.id.programList_userProgramListView);

        // create "delete" Button
        // by clicking it, checked items (programs) will be deleted
        mDeleteButton = (Button) v.findViewById(R.id.programList_deleteButton);

        // create "check all" Button for good user experience
        mProgramsCheckAllButton[0] = (Button) v.findViewById(R.id.programList_samplePrograms_checkAllButton);
        mProgramsCheckAllButton[1] = (Button) v.findViewById(R.id.programList_userPrograms_checkAllButton);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resizeDialog();

        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogFragment.Builder(ProgramListFragment.this)
                        .setTitle(R.string.confirmation)
                        .setMessage(getString(R.string.delete, generateProgramNamesToBeDeleted()))
                        .setPositiveButtonLabel(android.R.string.ok)
                        .setNegativeButtonLabel(android.R.string.cancel)
                        .show();
            }
        });

        for (int i = 0; i < NUM_PROGRAM_KINDS; i++) {
            final int dataId = i;
            mProgramsCheckAllButton[dataId].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAllItems(dataId);
                }
            });

            mProgramListView[dataId].setAdapter(mProgramDataAdapter[dataId]);
            mProgramListView[dataId].setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(
                        AdapterView<?> parent, View view, int position, long id) {
                    ProgramData data = mProgramDataAdapter[dataId].getItem(position);
                    CheckBox chk = (CheckBox) view.findViewById(R.id.programList_checkBox);
                    chk.setChecked(!chk.isChecked()); // toggle check

                    // update state
                    mCheckedPrograms.get(dataId).put(data.getProgramName(), chk.isChecked());
                }
            });
        }
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

    private void initializeProgramDataAdapter() {
        for (int dataId = 0; dataId < NUM_PROGRAM_KINDS; dataId++) {
            mCheckedPrograms.get(dataId).clear();

            // items are categorized into 2 parts; samples and user programs.
            ArrayList<String> programNames =
                    (dataId == SAMPLE_PROGRAM) ? mManager.loadSampleProgramNames() : mManager.loadUserProgramNames();
            // create an adapter for ListView
            List<ProgramData> data = new ArrayList<>();
            for (String programName : programNames) {
                ProgramData one = new ProgramData();
                one.setProgramName(programName);
                data.add(one);

                // initialize checked program list
                // put (program name, false)
                mCheckedPrograms.get(dataId).put(programName, false);
            }
            mProgramDataAdapter[dataId] = new ProgramDataAdapter(getActivity(), 0, data);
        }
    }

    private void checkAllItems(int dataId) {
        ListView lv = mProgramListView[dataId];
        Map<String, Boolean> m = mCheckedPrograms.get(dataId);
        ProgramDataAdapter pda = mProgramDataAdapter[dataId];

        for (int i = 0; i < lv.getChildCount(); i++) {
            View view = lv.getChildAt(i);
            CheckBox chk = (CheckBox) view.findViewById(R.id.programList_checkBox);
            chk.setChecked(true);

            m.put(chk.getText().toString(), true);
        }
        pda.notifyDataSetChanged();
    }

    private String generateProgramNamesToBeDeleted() {
        String separator = ", ";
        StringBuilder sb = new StringBuilder();
        for (int dataId = 0; dataId < NUM_PROGRAM_KINDS; dataId++) {
            for (Map.Entry<String, Boolean> e : mCheckedPrograms.get(dataId).entrySet()) {
                if (!e.getValue()) {
                    continue; // won't be deleted
                }

                // not initial addition
                if (sb.length() > 0) {
                    sb.append(separator);
                }
                sb.append(e.getKey());
            }
        }
        return sb.toString();
    }

    private void deletePrograms() {
        for (int dataId = 0; dataId < NUM_PROGRAM_KINDS; ++dataId) {
            for (Map.Entry<String, Boolean> e : mCheckedPrograms.get(dataId).entrySet()) {
                if (e.getValue()) {
                    if (dataId == SAMPLE_PROGRAM) { // sample programs
                        mManager.deleteSampleProgram(e.getKey());
                    }
                    else /* if (dataId == USER_PROGRAM) */ { // user programs
                        mManager.deleteUserProgram(e.getKey());
                    }
                }
            }
        }

        initializeProgramDataAdapter(); // reinitialize data
        for (int dataId = 0; dataId < NUM_PROGRAM_KINDS; dataId++) {
            mProgramListView[dataId].setAdapter(mProgramDataAdapter[dataId]);
            mProgramDataAdapter[dataId].notifyDataSetChanged();
        }
    }

    @Override
    public void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            deletePrograms();
        }
    }

    @Override
    public void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params) {

    }
}