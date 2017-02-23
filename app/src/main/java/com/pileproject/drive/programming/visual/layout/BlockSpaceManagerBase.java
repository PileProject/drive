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
package com.pileproject.drive.programming.visual.layout;

import android.content.Context;
import android.view.View;

import com.pileproject.drive.database.ProgramDataManager;
import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.ArrayList;
import java.util.List;

/**
 * A base manager class of {@link BlockSpaceLayout}. This class is responsible for some features of the layout such
 * as program loading.
 */
public abstract class BlockSpaceManagerBase {
    protected BlockSpaceLayout mLayout;
    protected Context mContext;
    private ProgramDataManager mManager;
    private static final String USER_PROGRAM_NAME_FORMAT = "%d";

    public BlockSpaceManagerBase(Context context, BlockSpaceLayout layout) {
        mContext = context;
        mLayout = layout;
        mManager = ProgramDataManager.getInstance();
    }

    /**
     * Saves the current program for execution.
     */
    public void saveExecutionProgram() {
        mManager.saveExecutionProgram(mLayout);
    }

    /**
     * Saves the current program as a sample program.
     * NOTE: if a name which is already saved is selected, it will be overwritten.
     *
     * @param programName a name of a sample program
     */
    public void saveSampleProgram(String programName) {
        mManager.saveSampleProgram(programName, mLayout);
    }

    /**
     * Saves the current program as a user program.
     * NOTE: the name of new program will be automatically generated.
     *
     * @return a name of a user program
     */
    public String saveUserProgram() {
        String newProgramName;

        // load user program names
        ArrayList<String> programs = mManager.loadUserProgramNames();
        if (programs.isEmpty()) {
            newProgramName = String.format(USER_PROGRAM_NAME_FORMAT, 1); // this is the first data
        }
        else {
            // generate a new program name
            // NOTE: this assumes the last program should have the largest number
            String lastName = programs.get(programs.size() - 1);
            int programNumber = Integer.parseInt(lastName); // e.g. 2
            newProgramName = String.format(USER_PROGRAM_NAME_FORMAT, programNumber + 1);
        }

        // save a new program with the name
        mManager.saveUserProgram(newProgramName, mLayout);
        return newProgramName;
    }

    /**
     * Loads a program for execution.
     */
    public void loadExecutionProgram() {
        placeBlocks(mManager.loadExecutionProgram());
    }

    /**
     * Loads a sample program.
     *
     * @param programName the name of the sample program to be loaded
     */
    public void loadSampleProgram(String programName) {
        placeBlocks(mManager.loadSampleProgram(programName));
    }

    /**
     * Loads a user program.
     *
     * @param programName the name of the user program to be loaded
     */
    public void loadUserProgram(String programName) {
        placeBlocks(mManager.loadUserProgram(programName));
    }

    /**
     * Loads names of sample programs.
     *
     * @return a list of names
     */
    public ArrayList<String> loadSampleProgramNames() {
        return mManager.loadSampleProgramNames();
    }

    /**
     * Load names of user programs.
     *
     * @return a list of names
     */
    public ArrayList<String> loadUserProgramNames() {
        return mManager.loadUserProgramNames();
    }

    /**
     * Adds all blocks into the layout that is managed by this class.
     * Please override this method to add some attributes (e.g., can be moved) to blocks.
     *
     * @param blocks the blocks to be added
     */
    public abstract void addBlocks(List<BlockBase> blocks);

    private void placeBlocks(List<BlockBase> data) {
        if (data.isEmpty()) {
            return;
        }

        addBlocks(data);

        // move all views to the old positions
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View view = mLayout.getChildAt(i);
            if (view instanceof BlockBase) {
                BlockBase b = (BlockBase) view;
                b.layout(b.getLeft(), b.getTop(), b.getLeft() + b.getMeasuredWidth(), b.getTop() + b.getMeasuredHeight());
            }
        }
    }

    /**
     * Deletes all blocks on the layout that is managed by this class. This method also deletes the previous execution
     * program.
     */
    public void deleteAllBlocks() {
        mLayout.removeAllViews();

        // remove the saved execution program
        mManager.deleteExecutionProgram();
    }
}
