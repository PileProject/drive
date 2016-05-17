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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pileproject.drive.R;
import com.pileproject.drive.preferences.CommonPreferences;
import com.pileproject.drive.preferences.MachinePreferences;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ProgrammingSpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Users create programs on this Activity
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public abstract class ProgrammingActivityBase extends AppCompatActivity {
    private final int NKINDS = 3;
    private final int ADD_BLOCK = 1;
    private final int EXECUTE_PROGRAM = 2;
    private final String TAG = "NxtProgrammingActivity";
    private List<Button> mAddBlockButtons;
    private Button mExecButton;
    private ProgrammingSpaceManager mSpaceManager;
    private boolean mIsConnected = false;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programming);

        setupNavigationView();

        findViews();

        mSpaceManager.loadExecutionProgram();

        // Set OnClickListeners to buttons that add blocks
        for (int i = 0; i < NKINDS; i++) {
            mAddBlockButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntentToBlockList();
                    intent.putExtra("category", mAddBlockButtons.indexOf(v));
                    Log.d(TAG, "category: " + mAddBlockButtons.indexOf(v));
                    startActivityForResult(intent, ADD_BLOCK);
                }
            });
        }

        mExecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAnotherActivity();
            }
        });
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    protected abstract Intent getIntentToBlockList();

    protected abstract Intent getIntentToDeviceList();

    protected abstract Intent getIntentToExecute();

    private void findViews() {
        mSpaceManager
                = new ProgrammingSpaceManager(this,
                                              (BlockSpaceLayout) findViewById(R.id.programming_placingBlockSpaceLayout));
        mAddBlockButtons = new ArrayList<>(NKINDS);
        mAddBlockButtons.add((Button) findViewById(R.id.programming_sequenceButton));
        mAddBlockButtons.add((Button) findViewById(R.id.programming_repetitionButton));
        mAddBlockButtons.add((Button) findViewById(R.id.programming_selectionButton));
        mExecButton = (Button) findViewById(R.id.programming_execButton);
    }

    private void moveToAnotherActivity() {
        mSpaceManager.saveExecutionProgram();
        String address = MachinePreferences.get(getApplicationContext()).getMacAddress();
        // TODO this check does not work when dissolves paring
        if (address == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(ProgrammingActivityBase.this).setTitle(R.string.error)
                    .setMessage(R.string.deviceselect_didNotSelectDevice)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(getIntentToDeviceList());
                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            Intent intent = getIntentToExecute();
            intent.putExtra("is_connected", mIsConnected);
            startActivityForResult(intent, EXECUTE_PROGRAM);
        }
    }

    public void onFinishSelectDialog(String selectedText, boolean isSample) {
        mSpaceManager.deleteAllBlocks(); // delete existing blocks
        if (isSample) { // load a sample program
            mSpaceManager.loadSampleProgram(selectedText);
        } else { // load a user program
            mSpaceManager.loadUserProgram(selectedText);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_BLOCK:
                if (resultCode == Activity.RESULT_OK) {
                    // Get results
                    int howToMake = data.getIntExtra("how_to_make", BlockFactory.SEQUENCE);
                    String blockName = data.getStringExtra("block_name");
                    ArrayList<BlockBase> blocks = BlockFactory.createBlocks(howToMake, blockName);
                    mSpaceManager.addBlocks(blocks);
                }
                break;

            case EXECUTE_PROGRAM:
                mIsConnected = !(data == null || !data.getBooleanExtra("is_connected", false));
                break;
        }
    }

    private void setupNavigationView() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.programming_navigationView);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.programming_toolbar);

        if (drawer == null || navView == null || toolbar == null) {
            return;
        }

        String deviceAddress = MachinePreferences.get(getApplicationContext()).getMacAddress();
        deviceAddress =
                (deviceAddress == null) ? getResources().getString(R.string.programming_noTargetDevice) : deviceAddress;

        toolbar.setTitle(getTitle() + ": " + deviceAddress);

        setSupportActionBar(toolbar);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                                                           drawer,
                                                           toolbar,
                                                           R.string.navigation_drawer_open,
                                                           R.string.navigation_drawer_close);

        drawer.addDrawerListener(mActionBarDrawerToggle);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.programming_menu_deleteAllBlocks: {
                        onDeleteAllBlocks();
                        break;
                    }

                    case R.id.programming_menu_goBackToTitle: {
                        mSpaceManager.saveExecutionProgram();
                        break;
                    }

                    case R.id.programming_menu_loadSampleProgram: {
                        ArrayList<String> array = mSpaceManager.loadSampleProgramNames();
                        CharSequence[] programs = array.toArray(new String[array.size()]);
                        ProgramListFragment sampleLoadFragment = ProgramListFragment.newInstance(programs, true);
                        sampleLoadFragment.show(getFragmentManager(), "sample_program_list");
                        break;
                    }

                    case R.id.programming_menu_saveProgram: {
                        onSaveProgram();
                        break;
                    }

                    case R.id.programming_menu_loadProgram: {
                        ArrayList<String> array = mSpaceManager.loadUserProgramNames();
                        CharSequence[] programs = array.toArray(new String[array.size()]);
                        ProgramListFragment loadFragment = ProgramListFragment.newInstance(programs, false);
                        loadFragment.show(getFragmentManager(), "program_list");
                        break;
                    }

                    default:
                        return false;
                }

                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private void onDeleteAllBlocks() {
        // Create an AlertDialog show confirmation
        new AlertDialog.Builder(this).setTitle(R.string.programming_deleteAllBlocks)
                .setMessage(R.string.confirmation)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           mSpaceManager.deleteAllBlocks();
                                       }
                                   })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void onSaveProgram() {
        boolean isEnabledSupervisorMode = CommonPreferences.get(getApplicationContext()).getSupervisorMode();

        if (isEnabledSupervisorMode) {
            // supervisor
            // save this program as a new sample one
            final InputSampleProgramNameDialogFragment fragment = new InputSampleProgramNameDialogFragment();
            fragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String programName = fragment.getInputtedProgramName();
                    mSpaceManager.saveSampleProgram(programName);
                    SaveProgramDialogFragment.newInstance(programName).show(getFragmentManager(), "save_program");
                }
            });
            fragment.show(getFragmentManager(), "input_program_name");
        } else {
            // not supervisor
            // save this program as a new one
            String programName = mSpaceManager.saveUserProgram();
            SaveProgramDialogFragment.newInstance(programName).show(getFragmentManager(), "save_program");
        }
    }

    public static class SaveProgramDialogFragment extends DialogFragment {
        private final static String KEY_PROGRAM_NAME = "program_name";

        private String mProgramName;

        public SaveProgramDialogFragment() {
            super();
        }

        public static SaveProgramDialogFragment newInstance(String programName) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_PROGRAM_NAME, programName);

            SaveProgramDialogFragment f = new SaveProgramDialogFragment();
            f.setArguments(bundle);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mProgramName = getArguments().getString(KEY_PROGRAM_NAME);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.programming_saveProgram)
                    .setMessage(getString(R.string.programming_savedAs, mProgramName))
                    .setPositiveButton(R.string.ok, null);
            return builder.create();
        }
    }

    public static class InputSampleProgramNameDialogFragment extends DialogFragment {
        private EditText mEditText;
        private DialogInterface.OnClickListener mOkClickListener = null;
        private DialogInterface.OnClickListener mCancelClickListener = null;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mEditText = new EditText(getActivity());
            mEditText.setMaxLines(1); // disable new lines

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.programming_saveProgramAsASample)
                    .setMessage(R.string.programming_inputProgramName)
                    .setView(mEditText)
                    .setPositiveButton(R.string.ok, mOkClickListener)
                    .setNegativeButton(R.string.cancel, mCancelClickListener);
            return builder.create();
        }

        public void setOnOkClickListener(DialogInterface.OnClickListener listener) {
            this.mOkClickListener = listener;
        }

        public void setOnCancelClickListener(DialogInterface.OnClickListener listener) {
            this.mCancelClickListener = listener;
        }

        public String getInputtedProgramName() {
            return mEditText.getText().toString();
        }
    }
}
