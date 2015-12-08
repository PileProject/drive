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

package com.pileproject.drive.setting.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.pileproject.drive.R;
import com.pileproject.drive.database.DBManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProgramListFragment extends Fragment {
    private DBManager mManager;
    private Button mDeleteButton;

    // 0 : Sample Programs, 1 : User Programs
    private Button[] mProgramsCheckAllButton = new Button[2];
    private ListView[] mProgramListView = new ListView[2];
    private ProgramDataAdapter[] mProgramDataAdapter =
            new ProgramDataAdapter[2];
    private ArrayList<Map<String, Boolean>> mCheckedPrograms =
            new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_programlist,
                                  container,
                                  false);

        // create DBManager to list programs
        mManager = new DBManager(getActivity());

        mCheckedPrograms.add(new LinkedHashMap<String, Boolean>());
        mCheckedPrograms.add(new LinkedHashMap<String, Boolean>());

        // prepare data for Data adapter
        initializeProgramDataAdapter();

        // create ListView to show all programs
        // an item in ListView consists of (checkbox, program name) [CustomView]
        mProgramListView[0] =
                (ListView) v.findViewById(R.id.programList_sampleProgramListView);
        mProgramListView[1] =
                (ListView) v.findViewById(R.id.programList_userProgramListView);

        // create "delete" Button
        // by clicking it, checked items (programs) will be deleted
        mDeleteButton = (Button) v.findViewById(R.id.programList_deleteButton);

        // create "check all" Button for good user experience
        mProgramsCheckAllButton[0] =
                (Button) v.findViewById(R.id.programList_samplePrograms_checkAllButton);
        mProgramsCheckAllButton[1] =
                (Button) v.findViewById(R.id.programList_userPrograms_checkAllButton);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmDialogFragment().show(getFragmentManager(),
                                                 "confirmation");
            }
        });

        for (int i = 0; i < 2; i++) {
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
                        AdapterView<?> parent,
                        View view,
                        int position,
                        long id) {
                    ProgramData data =
                            mProgramDataAdapter[dataId].getItem(position);
                    CheckBox chk =
                            (CheckBox) view.findViewById(R.id.programList_checkBox);
                    chk.setChecked(!chk.isChecked()); // toggle check

                    // update state
                    mCheckedPrograms.get(dataId)
                            .put(data.getProgramName(), chk.isChecked());
                }
            });
        }
    }

    private void initializeProgramDataAdapter() {
        for (int dataId = 0; dataId < 2; dataId++) {
            mCheckedPrograms.get(dataId).clear();

            // items are categorized into 2 parts; samples and user programs.
            String[] programNames = mManager.loadSavedProgramNames(dataId == 0);

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
            mProgramDataAdapter[dataId] =
                    new ProgramDataAdapter(getActivity(), 0, data);
        }
    }

    private void checkAllItems(int dataId) {
        ListView lv = mProgramListView[dataId];
        Map<String, Boolean> m = mCheckedPrograms.get(dataId);
        ProgramDataAdapter pda = mProgramDataAdapter[dataId];

        for (int i = 0; i < lv.getChildCount(); i++) {
            View view = lv.getChildAt(i);
            CheckBox chk =
                    (CheckBox) view.findViewById(R.id.programList_checkBox);
            chk.setChecked(true);

            m.put(chk.getText().toString(), true);
        }
        pda.notifyDataSetChanged();
    }

    private String generateProgramNamesToBeDeleted() {
        String separator = ", ";
        StringBuilder sb = new StringBuilder();
        for (int dataId = 0; dataId < 2; dataId++) {
            for (Map.Entry<String, Boolean> e : mCheckedPrograms.get(dataId)
                    .entrySet()) {
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
        for (int dataId = 0; dataId < 2; dataId++) {
            for (Map.Entry<String, Boolean> e : mCheckedPrograms.get(dataId)
                    .entrySet()) {
                if (e.getValue()) {
                    mManager.deleteProgramByName(e.getKey(), true);
                }
            }
        }
        initializeProgramDataAdapter(); // reinitialize data
        for (int dataId = 0; dataId < 2; dataId++) {
            mProgramListView[dataId].setAdapter(mProgramDataAdapter[dataId]);
            mProgramDataAdapter[dataId].notifyDataSetChanged();
        }
    }

    public class ConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.confirmation)
                    .setMessage(getString(R.string.delete,
                                          generateProgramNamesToBeDeleted()))
                    .setPositiveButton(R.string.ok,
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(
                                                   DialogInterface dialog,
                                                   int which) {
                                               deletePrograms();
                                           }
                                       })
                    .setNegativeButton(R.string.cancel, null);
            return builder.create();
        }
    }
}