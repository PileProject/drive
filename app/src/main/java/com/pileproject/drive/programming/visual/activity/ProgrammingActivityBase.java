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
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.pileproject.drive.util.development.DeployUtils;
import com.pileproject.drive.util.fragment.AlertDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Users create programs on this Activity
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public abstract class ProgrammingActivityBase extends AppCompatActivity implements AlertDialogFragment.EventListener {
    private static final int DIALOG_REQUEST_CODE_DID_NOT_SELECT_DEVICE = 10000;
    private static final int DIALOG_REQUEST_CODE_DELETE_ALL_BLOCK      = 20000;
    private static final int DIALOG_REQUEST_CODE_PROGRAM_LIST          = 30000;

    private static final String KEY_IS_SAMPLE       = "is_sample";
    private static final String KEY_SAMPLE_PROGRAMS = "samples_programs";

    private static final int ACTIVITY_RESULT_ADD_BLOCK       = 1;
    private static final int ACTIVITY_RESULT_EXECUTE_PROGRAM = 2;

    private final int NKINDS = 3;
    private List<Button> mAddBlockButtons;
    private Button mExecButton;
    private ProgrammingSpaceManager mSpaceManager;
    private boolean mIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programming);

        setupToolbar();

        findViews();

        mSpaceManager.loadExecutionProgram();

        // Set OnClickListeners to buttons that add blocks
        for (int i = 0; i < NKINDS; i++) {
            mAddBlockButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntentToBlockList();
                    intent.putExtra(getString(R.string.key_block_category), mAddBlockButtons.indexOf(v));
                    startActivityForResult(intent, ACTIVITY_RESULT_ADD_BLOCK);
                }
            });
        }

        mExecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToExecutionActivity();
            }
        });
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

    private void moveToExecutionActivity() {
        mSpaceManager.saveExecutionProgram();
        String address = MachinePreferences.get(getApplicationContext()).getMacAddress();

        // TODO: this check does not work when dissolves paring
        if (address != null || DeployUtils.isOnEmulator()) {
            Intent intent = getIntentToExecute();
            intent.putExtra(getString(R.string.key_execution_isConnected), mIsConnected);
            startActivityForResult(intent, ACTIVITY_RESULT_EXECUTE_PROGRAM);
            return ;
        }

        new AlertDialogFragment.Builder(this)
                .setRequestCode(DIALOG_REQUEST_CODE_DID_NOT_SELECT_DEVICE)
                .setMessage(R.string.setting_bluetoothMachineSelect_didNotSelectDevice)
                .setPositiveButtonLabel(android.R.string.ok)
                .setCancelable(false)
                .show();
    }

    protected void moveToTitleActivity() {
        mSpaceManager.saveExecutionProgram();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_RESULT_ADD_BLOCK:
                if (resultCode == Activity.RESULT_OK) {
                    // Get results
                    int howToMake = data.getIntExtra(getString(R.string.key_block_how_to_make), BlockFactory.SEQUENCE);
                    String blockName = data.getStringExtra(getString(R.string.key_block_block_name));
                    List<BlockBase> blocks = BlockFactory.createBlocks(howToMake, blockName);
                    mSpaceManager.addBlocks(blocks);
                }
                break;

            case ACTIVITY_RESULT_EXECUTE_PROGRAM:
                mIsConnected = !(data == null || !data.getBooleanExtra(getString(R.string.key_execution_isConnected), false));
                break;
        }
    }

    private void setupToolbar() {
        // get device address to show it on toolbar
        String deviceAddress = MachinePreferences.get(getApplicationContext()).getMacAddress();
        deviceAddress =
                (deviceAddress == null) ? getResources().getString(R.string.programming_noTargetDevice) : deviceAddress;

        Toolbar toolbar = (Toolbar) findViewById(R.id.programming_toolbar);
        toolbar.inflateMenu(R.menu.menu_programming);
        toolbar.setTitle(deviceAddress);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.programming_menu_deleteAllBlocks: {
                        showDeleteBlockDialog();
                        break;
                    }

                    case R.id.programming_menu_goBackToTitle: {
                        moveToTitleActivity();
                        break;
                    }

                    case R.id.programming_menu_loadSampleProgram: {
                        showLoadProgramDialog(mSpaceManager.loadSampleProgramNames(), true);
                        break;
                    }

                    case R.id.programming_menu_saveProgram: {
                        showSaveProgramDialog();
                        break;
                    }

                    case R.id.programming_menu_loadProgram: {
                        showLoadProgramDialog(mSpaceManager.loadUserProgramNames(), false);
                        break;
                    }

                    default:
                        return false;
                }

                return true;
            }
        });
    }

    private void showSaveProgramDialog() {
        boolean isInSupervisorMode = CommonPreferences.get(getApplicationContext()).getSupervisorMode();

        if (isInSupervisorMode) {
            // supervisor
            // save this program as a new sample one
            InputSampleProgramNameDialogFragment fragment = new InputSampleProgramNameDialogFragment();
            fragment.show(getSupportFragmentManager(), "TAG");
        } else {
            // not supervisor
            // save this program as a new one
            String programName = mSpaceManager.saveUserProgram();

            new AlertDialogFragment.Builder(this)
                        .setTitle(R.string.programming_saveProgram)
                        .setMessage(getString(R.string.programming_savedAs, programName))
                        .setPositiveButtonLabel(android.R.string.ok)
                        .show();
        }
    }

    private void showLoadProgramDialog(ArrayList<String> programs, boolean isSample) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_SAMPLE, isSample);
        args.putStringArrayList(KEY_SAMPLE_PROGRAMS, programs);

        new AlertDialogFragment.Builder(this)
                    .setRequestCode(DIALOG_REQUEST_CODE_PROGRAM_LIST)
                    .setTitle(R.string.programming_loadProgram)
                    .setItems(programs.toArray(new String[programs.size()]))
                    .setNegativeButtonLabel(android.R.string.cancel)
                    .setParams(args)
                    .show();
    }

    private void showDeleteBlockDialog() {
        new AlertDialogFragment.Builder(this)
                    .setRequestCode(DIALOG_REQUEST_CODE_DELETE_ALL_BLOCK)
                    .setTitle(R.string.programming_deleteAllBlocks)
                    .setMessage(R.string.confirmation)
                    .setPositiveButtonLabel(android.R.string.ok)
                    .setNegativeButtonLabel(android.R.string.cancel)
                    .show();
    }


    @Override
    public void onDialogEventHandled(int requestCode, DialogInterface dialog, int which, Bundle params) {
        switch (requestCode) {
            case DIALOG_REQUEST_CODE_DID_NOT_SELECT_DEVICE: {
                startActivity(getIntentToDeviceList());
                break;
            }

            case DIALOG_REQUEST_CODE_DELETE_ALL_BLOCK: {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    break;
                }

                mSpaceManager.deleteAllBlocks();
                break;
            }

            case DIALOG_REQUEST_CODE_PROGRAM_LIST: {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    break;
                }

                boolean isSample = params.getBoolean(KEY_IS_SAMPLE);
                ArrayList<String> programs = params.getStringArrayList(KEY_SAMPLE_PROGRAMS);
                String programName = programs.get(which);

                mSpaceManager.deleteAllBlocks(); // delete existing blocks

                if (isSample) {
                    mSpaceManager.loadSampleProgram(programName);
                } else {
                    mSpaceManager.loadUserProgram(programName);
                }

                break;
            }
        }
    }

    @Override
    public void onDialogEventCancelled(int requestCode, DialogInterface dialog, Bundle params) {

    }

    private void saveSampleProgram(String programName) {
        mSpaceManager.saveSampleProgram(programName);

        new AlertDialogFragment.Builder(this)
                .setTitle(R.string.programming_saveProgram)
                .setPositiveButtonLabel(android.R.string.ok)
                .setMessage(getString(R.string.programming_savedAs, programName))
                .show();
    }

    public static class InputSampleProgramNameDialogFragment extends DialogFragment {
        private EditText mEditText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mEditText = new EditText(getActivity());
            mEditText.setMaxLines(1); // disable new lines

            DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String programName = mEditText.getText().toString();
                        ((ProgrammingActivityBase) getActivity()).saveSampleProgram(programName);
                    }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.programming_saveProgramAsASample)
                    .setMessage(R.string.programming_inputProgramName)
                    .setView(mEditText)
                    .setPositiveButton(R.string.ok, okListener);

            return builder.create();
        }
    }
}
