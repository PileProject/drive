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
package com.pileproject.drive.database;

import android.view.View;

import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.programming.visual.activity.BlockPositionComparator;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.block.NumberTextHolder;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A manger of program data which is based on {@link ProgramSpec} and {@link ProgramDataSpec}.
 */
public class ProgramDataManager {
    private static ProgramDataManager mInstance = new ProgramDataManager();
    private DriveDatabase mDriveDatabase;

    // the number of execution programs should be less than or equals to 1
    private static final Query EXECUTION_PROGRAM =
            Query.select(Program.PROPERTIES)
                    .from(Program.TABLE)
                    .where(Program.TYPE.eq(Program.EXECUTION))
                    .freeze();

    private static final Query USER_PROGRAMS =
            Query.select(Program.PROPERTIES)
                    .from(Program.TABLE)
                    .where(Program.TYPE.eq(Program.USER))
                    .orderBy(Program.UPDATED_AT.asc())
                    .freeze();

    private static final Query SAMPLE_PROGRAMS =
            Query.select(Program.PROPERTIES)
                    .from(Program.TABLE)
                    .where(Program.TYPE.eq(Program.SAMPLE))
                    .orderBy(Program.UPDATED_AT.asc())
                    .freeze();

    private ProgramDataManager() {
        if (mDriveDatabase == null) {
            mDriveDatabase = new DriveDatabase(DriveApplication.getContext());
        }
    }

    public static ProgramDataManager getInstance() {
        return mInstance;
    }

    /**
     * Saves a program temporarily to execute it.
     *
     * @param layout the programming space that has blocks
     * @return succeed (<code>true</code>) or not (<code>false</code>)
     */
    public boolean saveExecutionProgram(BlockSpaceLayout layout) {
        return saveProgram(Program.EXECUTION, Program.EXECUTION, layout);
    }

    /**
     * Saves a sample program.
     *
     * @param programName the name of a new program
     * @param layout the programming space that has blocks
     * @return succeed (<code>true</code>) or not (<code>false</code>)
     */
    public boolean saveSampleProgram(String programName, BlockSpaceLayout layout) {
        return saveProgram(programName, Program.SAMPLE, layout);
    }

    /**
     * Saves a user program.
     *
     * @param programName the name of a new program
     * @param layout the programming space that has blocks
     * @return succeed (<code>true</code>) or not (<code>false</code>)
     */
    public boolean saveUserProgram(String programName, BlockSpaceLayout layout) {
        return saveProgram(programName, Program.USER, layout);
    }

    private boolean saveProgram(String programName, String programType, BlockSpaceLayout layout) {
        // delete the old program with 'programName' and 'programType'
        deleteProgram(programName, programType);

        // save new program
        Program program = new Program()
                .setName(programName)
                .setType(programType)
                .setUpdatedAt(System.currentTimeMillis());
        // save the program (persist will set the _id for the target item)
        if (!mDriveDatabase.persist(program)) {
            return false;   // failed to save
        }

        // insert all views attached to the layout
        for (int i = 0; i < layout.getChildCount(); ++i) {
            View view = layout.getChildAt(i);
            // check this view is a child of BlockBase or not
            if (!(view instanceof BlockBase)) {
                continue;
            }

            BlockBase b = (BlockBase) view;
            ProgramData data = new ProgramData()
                    .setProgramId(program.getId())
                    .setType(b.getClass().getName())
                    .setLeft(b.getLeft())
                    .setTop(b.getTop());
            // get the number of TextView if the block has one
            if (b instanceof NumberTextHolder) {
                data.setNumber(((NumberTextHolder) b).getValueAsString());
            }

            // save the data
            if (!mDriveDatabase.persist(data)) {
                return false; // failed to save
            }
        }
        return true;
    }

    /**
     * Loads an execution program's block data (sorted with {@link BlockPositionComparator} in ascending order).
     *
     * @return a loaded data as {@link ArrayList}
     */
    public ArrayList<BlockBase> loadExecutionProgram() {
        return loadProgram(Program.EXECUTION, Program.EXECUTION);
    }

    /**
     * Loads a sample program's block data (sorted with {@link BlockPositionComparator} in ascending order).
     *
     * @param programName the name of a sample program
     * @return a loaded data as {@link ArrayList}
     */
    public ArrayList<BlockBase> loadSampleProgram(String programName) {
        return loadProgram(programName, Program.SAMPLE);
    }

    /**
     * Loads a user program's block data (sorted with {@link BlockPositionComparator} in ascending order).
     *
     * @param programName the name of a user program
     * @return a loaded data as {@link ArrayList}
     */
    public ArrayList<BlockBase> loadUserProgram(String programName) {
        return loadProgram(programName, Program.USER);
    }

    private ArrayList<BlockBase> loadProgram(String programName, String programType) {
        // search a program with 'programName' and 'programType'
        Query q = Query
                .select(Program.ID)
                .from(Program.TABLE)
                .where(Program.NAME.eq(programName)
                               .and(Program.TYPE.eq(programType)));
        Program program = mDriveDatabase.fetchByQuery(Program.class, q);
        if (program == null) { // such a program is not found
            return new ArrayList<>();
        }

        // search program data with the program id
        q = Query.select(ProgramData.PROPERTIES)
                .from(ProgramData.TABLE)
                .where(ProgramData.PROGRAM_ID.eq(program.getId()));
        SquidCursor<ProgramData> programData = mDriveDatabase.query(ProgramData.class, q);
        return loadBlocks(programData);
    }

    private ArrayList<BlockBase> loadBlocks(SquidCursor<ProgramData> c) {
        ArrayList<BlockBase> blocks = new ArrayList<>();

        ProgramData data = new ProgramData();
        try {
            while (c.moveToNext()) {
                data.readPropertiesFromCursor(c);
                // create a BlockBase with a type (= name)
                BlockBase b = BlockFactory.createBlock(data.getType());
                // set data's properties
                b.setLeft(data.getLeft());
                b.setTop(data.getTop());
                if (b instanceof NumberTextHolder) {
                    ((NumberTextHolder) b).setValueAsString(data.getNumber());
                }
                // add the block to a list
                blocks.add(b);
            }
        }
        finally {
            c.close(); // close the cursor
        }
        // sort blocks with their positions
        Collections.sort(blocks, new BlockPositionComparator());
        return blocks;
    }

    /**
     * Loads all sample program names.
     *
     * @return the names of sample programs as {@link ArrayList}
     */
    public ArrayList<String> loadSampleProgramNames() {
        return loadProgramNames(Program.SAMPLE);
    }

    /**
     * Loads all user program names.
     *
     * @return the names of user programs as {@link ArrayList}
     */
    public ArrayList<String> loadUserProgramNames() {
        return loadProgramNames(Program.USER);
    }

    private ArrayList<String> loadProgramNames(String programType) {
        Query q;
        if (programType.equals(Program.USER)) {
            q = USER_PROGRAMS;
        }
        else if (programType.equals(Program.SAMPLE)) {
            q = SAMPLE_PROGRAMS;
        }
        else { // do not allow users to check 'EXECUTION' programs directly
            return new ArrayList<>();
        }
        // execute query
        SquidCursor<Program> programs = mDriveDatabase.query(Program.class, q);
        ArrayList<String> programNames = new ArrayList<>();
        while (programs.moveToNext()) {
            programNames.add(programs.get(Program.NAME));
        }
        programs.close(); // close the cursor
        return programNames;
    }

    /**
     * Deletes an execution program.
     */
    public void deleteExecutionProgram() {
        deleteProgram(Program.EXECUTION, Program.EXECUTION);
    }

    /**
     * Deletes a sample program with <code>programName</code>.
     *
     * @param programName the name of a sample program
     */
    public void deleteSampleProgram(String programName) {
        deleteProgram(programName, Program.SAMPLE);
    }
    /**
     * Deletes a user program with <code>programName</code>.
     *
     * @param programName the name of a user program
     */
    public void deleteUserProgram(String programName) {
        deleteProgram(programName, Program.USER);
    }

    private void deleteProgram(String programName, String programType) {
        mDriveDatabase.deleteWhere(Program.class,
                                   Program.NAME.eq(programName)
                                        .and(Program.TYPE.eq(programType)));
    }
}