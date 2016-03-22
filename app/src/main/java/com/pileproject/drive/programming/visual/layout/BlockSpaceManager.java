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

package com.pileproject.drive.programming.visual.layout;

import android.content.Context;
import android.view.View;

import com.pileproject.drive.database.ProgramDataManager;
import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.ArrayList;

/**
 * Manager of BlockSpaceLayout
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public abstract class BlockSpaceManager {
    protected BlockSpaceLayout mLayout = null;
    protected Context mContext;
    private ProgramDataManager mManager;
    private static final String USER_PROGRAM_NAME_FORMAT = "%d";

    public BlockSpaceManager(Context context, BlockSpaceLayout layout) {
        mContext = context;
        mLayout = layout;
        mManager = ProgramDataManager.getInstance();
    }

    public void saveExecutionProgram() {
        mManager.saveExecutionProgram(mLayout);
    }

    public void saveSampleProgram(String programName) {
        mManager.saveSampleProgram(programName, mLayout);
    }

    public String saveUserProgram() {
        // generate a new program name automatically for a user program
        String newProgramName;

        // load user program names
        ArrayList<String> programs = mManager.loadUserProgramNames();
        if (programs.isEmpty()) {
            newProgramName = String.format(USER_PROGRAM_NAME_FORMAT, 1); // this is the first data
        }
        else {
            // generate a new program name
            String lastName = programs.get(programs.size() - 1);
            int programNumber = Integer.parseInt(lastName); // e.g. 2
            newProgramName = String.format(USER_PROGRAM_NAME_FORMAT, programNumber + 1);
        }

        // save a new program with the name
        mManager.saveUserProgram(newProgramName, mLayout);
        return newProgramName;
    }

    public void loadExecutionProgram() {
        placeBlocks(mManager.loadExecutionProgram());
    }

    public void loadSampleProgram(String programName) {
        placeBlocks(mManager.loadSampleProgram(programName));
    }

    public void loadUserProgram(String programName) {
        placeBlocks(mManager.loadUserProgram(programName));
    }

    public ArrayList<String> loadSampleProgramNames() {
        return mManager.loadSampleProgramNames();
    }

    public ArrayList<String> loadUserProgramNames() {
        return mManager.loadUserProgramNames();
    }

    public abstract void addBlocks(ArrayList<BlockBase> blocks);

    private void placeBlocks(ArrayList<BlockBase> data) {
        if (data.isEmpty()) {
            return;
        }

        addBlocks(data);

        // Move all Views to old positions
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View view = mLayout.getChildAt(i);
            if (view instanceof BlockBase) {
                BlockBase b = (BlockBase) view;
                b.layout(b.getLeft(), b.getTop(), b.getLeft() + b.getMeasuredWidth(), b.getTop() + b.getMeasuredHeight());
            }
        }
    }

    public void deleteAllBlocks() {
        mLayout.removeAllViews();
        mManager.deleteExecutionProgram();
    }
}
