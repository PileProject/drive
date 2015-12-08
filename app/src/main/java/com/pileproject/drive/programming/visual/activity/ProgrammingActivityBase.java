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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.pileproject.drive.programming.visual.layout.ProgrammingSpaceManager;
import com.pileproject.drive.setting.app.SupervisorModeFragment;
import com.pileproject.drive.util.SharedPreferencesWrapper;

import java.util.ArrayList;

/**
 * Users create programs on this Activity
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
@SuppressWarnings("deprecation")
public abstract class ProgrammingActivityBase extends Activity implements OnItemClickListener {

    private final int NKINDS = 3;
    private final int ADD_BLOCK = 1;
    private final int EXECUTE_PROGRAM = 2;
    private final String TAG = "NxtProgrammingActivity";
    private ArrayList<Button> mAddBlockButtons;
    private Button mExecButton;
    private UndoAndRedoButtonsManager mButtonsManager;
    private ListView mMenuList;
    private SlidingDrawer mMenuHandle;
    private ProgrammingSpaceManager mSpaceManager;
    private boolean mIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programming);

        // Set context to BlockFactory to create blocks
        BlockFactory.setContext(getApplicationContext());

        findViews();

        mSpaceManager.load();

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

        // Set ArrayAdapter to ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapter.add(getString(R.string.programming_deleteAllBlocks));
        adapter.add(getString(R.string.programming_goBackToTitle));

        adapter.add(getString(R.string.programming_loadSampleProgram));
        adapter.add(getString(R.string.programming_saveProgram));
        adapter.add(getString(R.string.programming_loadProgram));

        mMenuList.setAdapter(adapter);
        mMenuList.setOnItemClickListener(this);

        // set listeners for changing handler's image
        mMenuHandle.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDrawerClosed() {
                LinearLayout ll = (LinearLayout) findViewById(R.id.programming_menuDrawerHandle);
                int sdk = android.os.Build.VERSION.SDK_INT;

                // check the sdk if older than Jelly bean
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.to_left));
                } else {
                    // this code won't work in older version than Jelly bean
                    ll.setBackground(getResources().getDrawable(R.drawable.to_left));
                }
            }
        });
        mMenuHandle.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDrawerOpened() {
                LinearLayout ll = (LinearLayout) findViewById(R.id.programming_menuDrawerHandle);
                int sdk = android.os.Build.VERSION.SDK_INT;

                // check the sdk if older than Jelly bean
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.to_right));
                } else {
                    // this code won't work in older version than Jelly bean
                    ll.setBackground(getResources().getDrawable(R.drawable.to_right));
                }
            }
        });
        String deviceAddress = SharedPreferencesWrapper.loadDefaultDeviceAddress(this);
        if (deviceAddress != null) {
            CharSequence title = getTitle();
            setTitle(title + ": " +
                             getResources().getString(R.string.programming_targetDevice) +
                             deviceAddress);
        } else {
            CharSequence title = getTitle();
            setTitle(title + ": " +
                             getResources().getString(R.string.programming_noTargetDevice));
        }
    }

    protected abstract Intent getIntentToBlockList();

    protected abstract Intent getIntentToDeviceList();

    protected abstract Intent getIntentToExecute();

    private void findViews() {
        mButtonsManager = new UndoAndRedoButtonsManager((Button) findViewById(R.id.programming_undoButton),
                                                        (Button) findViewById(R.id.programming_redoButton));
        mSpaceManager = new ProgrammingSpaceManager(this,
                                                    (BlockSpaceLayout) findViewById(R.id.programming_placingBlockSpaceLayout),
                                                    mButtonsManager);
        mAddBlockButtons = new ArrayList<>(NKINDS);
        mAddBlockButtons.add((Button) findViewById(R.id.programming_sequenceButton));
        mAddBlockButtons.add((Button) findViewById(R.id.programming_repetitionButton));
        mAddBlockButtons.add((Button) findViewById(R.id.programming_selectionButton));
        mExecButton = (Button) findViewById(R.id.programming_execButton);

        mMenuList = (ListView) findViewById(R.id.programming_menuList);
        mMenuHandle = (SlidingDrawer) findViewById(R.id.programming_menuDrawer);
    }

    private void moveToAnotherActivity() {
        mSpaceManager.save();
        String address = SharedPreferencesWrapper.loadDefaultDeviceAddress(getApplicationContext());
        // TODO this check does not work when dissolves paring
        if (address == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(ProgrammingActivityBase.this).setTitle(R.string.error)
                    .setMessage(R.string.deviceselect_didNotSelectDevice)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(
                                                   DialogInterface dialog, int which) {
                                               Intent intent = getIntentToDeviceList();
                                               startActivity(intent);
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
        mSpaceManager.loadByName(selectedText, isSample); // load programs
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
                    mButtonsManager.checkButtonsWorkability();
                }
                break;

            case EXECUTE_PROGRAM:
                mIsConnected = !(data == null || !data.getBooleanExtra("is_connected", false));
                break;
        }
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        ListView list = (ListView) parent;
        String item = (String) list.getItemAtPosition(position);

        // Initializing layout
        if (item.equals(getString(R.string.programming_deleteAllBlocks))) {
            // Create an AlertDialog show confirmation
            new AlertDialog.Builder(this).setTitle(R.string.programming_deleteAllBlocks)
                    .setMessage(R.string.confirmation)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(
                                                   DialogInterface dialog, int which) {
                                               mSpaceManager.deleteAllBlocks();
                                               mButtonsManager.checkButtonsWorkability();
                                               mMenuHandle.close();
                                           }
                                       })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
        // Going back to NxtTitleActivity
        else if (item.equals(getString(R.string.programming_goBackToTitle))) {
            mSpaceManager.save();
            finish();
        } else if (item.equals(getString(R.string.programming_loadSampleProgram))) {
            CharSequence[] programs = mSpaceManager.loadSavedSampleProgramNames();
            DialogFragment programListFragment = new ProgramListFragment(programs, true);
            programListFragment.show(getFragmentManager(), "sample_program_list");
            mMenuHandle.close();
        } else if (item.equals(getString(R.string.programming_saveProgram))) {
            boolean isEnabledSupervisorMode =
                    SharedPreferencesWrapper.loadBoolPreference(this, SupervisorModeFragment.class.getName(), false);
            if (isEnabledSupervisorMode) {
                // supervisor
                // save this program as a new sample one
                final InputSampleProgramNameDialogFragment fragment = new InputSampleProgramNameDialogFragment();
                fragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String programName = fragment.getInputtedProgramName();
                        mSpaceManager.saveWithName(programName, true);
                        showSavedProgramNameDialog(programName);
                    }
                });
                fragment.show(getFragmentManager(), "input_program_name");
                mMenuHandle.close();
            } else {
                // not supervisor
                // save this program as a new one
                String programName = mSpaceManager.saveAsNew();
                showSavedProgramNameDialog(programName);
                mMenuHandle.close();
            }
        } else if (item.equals(getString(R.string.programming_loadProgram))) {
            CharSequence[] programs = mSpaceManager.loadSavedProgramNames();
            DialogFragment programListFragment = new ProgramListFragment(programs);
            programListFragment.show(getFragmentManager(), "program_list");
            mMenuHandle.close();
        }
    }

    public void showSavedProgramNameDialog(String programName) {
        new SaveProgramDialogFragment(programName).show(getFragmentManager(), "save_program");
    }

    public static class SaveProgramDialogFragment extends DialogFragment {
        private String mProgramName;

        public SaveProgramDialogFragment(String programName) {
            mProgramName = programName;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        public void setOnOkClickListener(
                DialogInterface.OnClickListener listener) {
            this.mOkClickListener = listener;
        }

        public void setOnCancelClickListener(
                DialogInterface.OnClickListener listener) {
            this.mCancelClickListener = listener;
        }

        public String getInputtedProgramName() {
            return mEditText.getText().toString();
        }
    }

    /**
     * The manager of Undo and Redo buttons.
     * This check the workability of buttons and
     * enable or disable them.
     *
     * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
     */
    public class UndoAndRedoButtonsManager {
        private Button mUndoButton;
        private Button mRedoButton;

        public UndoAndRedoButtonsManager(Button undoButton, Button redoButton) {
            mUndoButton = undoButton;
            mRedoButton = redoButton;

            mUndoButton.setEnabled(false);
            mUndoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSpaceManager.undo();
                    checkButtonsWorkability();
                }
            });

            mRedoButton.setEnabled(false);
            mRedoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSpaceManager.redo();
                    checkButtonsWorkability();
                }
            });
        }

        public void checkButtonsWorkability() {
            mUndoButton.setEnabled(mSpaceManager.canUndo());
            mRedoButton.setEnabled(mSpaceManager.canRedo());
        }
    }
}