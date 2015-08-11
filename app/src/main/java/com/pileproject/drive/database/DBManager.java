/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.pileproject.drive.programming.visual.activity.BlockPositionComparator;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.block.NumTextHolder;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Database manager
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public class DBManager {
	private DBOpenHelper mHelper;
	private SQLiteDatabase mDb;
	
	private static final String TAG = "DBManager";
	
	/**
	 * Constructor
	 * 
	 * @param context
	 *            The context of Activity that calls this manager
	 */
	public DBManager(Context context) {
		// Use Helper class
		mHelper = DBOpenHelper.getInstance(context);
		
		if (mHelper != null) {
			mDb = mHelper.getWritableDatabase();
		}
		else {
			mDb = null;
		}
	}
	
	/**
	 * Close Database
	 */
	public void close() {
		mDb.close();
	}
	
	/**
	 * Save program data temporarily to execute it
	 * 
	 * @param layout
	 *            The programming space that has blocks
	 */
	public void saveAllBlocks(BlockSpaceLayout layout) {
		deleteAll(); // Firstly delete all previous data
		
		// Insert all View attached to the layout
		for (int i = 0; i < layout.getChildCount(); i++) {
			ContentValues val = new ContentValues();
			
			View view = layout.getChildAt(i);
			// Check this view is a child of BlockBase or not
			if (!(view instanceof BlockBase)) {
				Log.d(TAG, "Not BlockBase: " + view.getClass().getSimpleName());
				continue;
			}
			
			BlockBase b = (BlockBase) view;
			Log.d(TAG, "BlockBase: " + view.getClass().getSimpleName());
			
			val.put(DBOpenHelper.BLOCK_NAME, b.getClass().getName());
			val.put(DBOpenHelper.BLOCK_LEFT, b.getLeft());
			val.put(DBOpenHelper.BLOCK_TOP, b.getTop());
			val.put(DBOpenHelper.BLOCK_RIGHT, b.getRight());
			val.put(DBOpenHelper.BLOCK_BOTTOM, b.getBottom());
			
			// Get the number of TextView if the block has one
			if (b instanceof NumTextHolder) {
				val.put(DBOpenHelper.BLOCK_NUM, ((NumTextHolder) b).getNum());
			}
			else {
				val.put(DBOpenHelper.BLOCK_NUM, 0);
			}
			
			// Save
			mDb.insert(DBOpenHelper.TBL_PROGRAM_DATA, null, val);
		}
	}
	
	private int getProgramIdByName(String programName, boolean isSample) {
		String selection = DBOpenHelper.PROGRAM_NAME + " = ? AND " + DBOpenHelper.IS_SAMPLE + " = ? ";
		String[] selectionArgs = {
			programName, isSample ? "1" : "0"
		};
		
		Cursor c = mDb.query(DBOpenHelper.TBL_SAVED_PROGRAMS,
				new String[] {
					"_id",
				}, selection, selectionArgs, null, null, null);
		
		// if the program name has not been saved
		if (c.getCount() == 0) {
			c.close();
			return -1;
		}
		
		c.moveToFirst();
		int programId = c.getInt(0);
		c.close();
		return programId;
	}
	
	private int insertProgramName(String programName, boolean isSample) {
		// add new program
		ContentValues val = new ContentValues();
		val.put(DBOpenHelper.PROGRAM_NAME, programName);
		val.put(DBOpenHelper.IS_SAMPLE, isSample ? 1 : 0);
		mDb.insert(DBOpenHelper.TBL_SAVED_PROGRAMS, null, val);
		
		// query again
		return getProgramIdByName(programName, isSample);
	}
	
	public String createNewProgramName() {
		String selection = DBOpenHelper.IS_SAMPLE + " = ?";
		String[] selectionArgs = {
			"0"
		}; // false = not sample
		Cursor c = mDb.query(DBOpenHelper.TBL_SAVED_PROGRAMS,
				new String[] {
					// this assumes the name of users's programs are integer strings
					"max(cast(program_name as integer))"
				}, selection, selectionArgs, null, null, null);
		
		c.moveToFirst();
		int nextId = c.getInt(0) + 1;
		c.close();
		return nextId + "";
	}
	
	public void saveWithName(String programName, BlockSpaceLayout layout) {
		saveWithName(programName, layout, false); // not sample (default)
	}
	
	public void saveWithName(String programName, BlockSpaceLayout layout, boolean isSample) {
		int programId = getProgramIdByName(programName, isSample);
		
		if (programId < 0) {
			// new program (= not exists)
			programId = insertProgramName(programName, isSample);
		}
		else {
			// delete old data
			String selection = DBOpenHelper.SAVED_PROGRAM_ID + " = ?";
			String[] selectionArgs = {
				programId + ""
			};
			mDb.delete(DBOpenHelper.TBL_SAVED_PROGRAM_DATA, selection, selectionArgs);
		}
		// Insert all Views attached to the layout
		for (int i = 0; i < layout.getChildCount(); i++) {
			ContentValues val = new ContentValues();
			
			View view = layout.getChildAt(i);
			// Check this view is a child of BlockBase or not
			if (!(view instanceof BlockBase)) {
				Log.d(TAG, "Not BlockBase: " + view.getClass().getSimpleName());
				continue;
			}
			
			BlockBase b = (BlockBase) view;
			Log.d(TAG, "BlockBase: " + view.getClass().getSimpleName());
			
			val.put(DBOpenHelper.SAVED_PROGRAM_ID, programId);
			val.put(DBOpenHelper.BLOCK_NAME, b.getClass().getName());
			val.put(DBOpenHelper.BLOCK_LEFT, b.getLeft());
			val.put(DBOpenHelper.BLOCK_TOP, b.getTop());
			val.put(DBOpenHelper.BLOCK_RIGHT, b.getRight());
			val.put(DBOpenHelper.BLOCK_BOTTOM, b.getBottom());
			
			// Get the number of TextView if the block has one
			if (b instanceof NumTextHolder) {
				val.put(DBOpenHelper.BLOCK_NUM, ((NumTextHolder) b).getNum());
			}
			else {
				val.put(DBOpenHelper.BLOCK_NUM, 0);
			}
			
			// Save
			mDb.insert(DBOpenHelper.TBL_SAVED_PROGRAM_DATA, null, val);
		}
	}
	
	private ArrayList<BlockBase> loadBlocks(Cursor c) {
		int numRows = c.getCount();
		ArrayList<BlockBase> blocks = new ArrayList<BlockBase>(numRows);
		
		c.moveToFirst();
		for (int i = 0; i < numRows; i++) {
			BlockBase b = BlockFactory.createBlocks(BlockFactory.LOAD, c.getString(0)).get(0);
			
			b.left = c.getInt(1);
			b.top = c.getInt(2);
			b.right = c.getInt(3);
			b.bottom = c.getInt(4);
			if (b instanceof NumTextHolder) {
				((NumTextHolder) b).setNum(c.getInt(5));
			}
			
			blocks.add(b);
			c.moveToNext();
		}
		c.close();
		Collections.sort(blocks, new BlockPositionComparator());
		return blocks;
	}
	
	/**
	 * Load all block data (Sorted)
	 * 
	 * @return ArrayList<BlockBase> loaded data
	 */
	public ArrayList<BlockBase> loadAll() {
		if (mDb == null) return null;
		Cursor c = mDb.query(DBOpenHelper.TBL_PROGRAM_DATA,
				new String[] {
					DBOpenHelper.BLOCK_NAME,
					DBOpenHelper.BLOCK_LEFT,
					DBOpenHelper.BLOCK_TOP,
					DBOpenHelper.BLOCK_RIGHT,
					DBOpenHelper.BLOCK_BOTTOM,
					DBOpenHelper.BLOCK_NUM,
				}, null, null, null, null, null);
		
		return loadBlocks(c);
	}
	
	/**
	 * Load block data (Sorted)
	 * 
	 * @param programName
	 *            program name
	 * @return ArrayList<BlockBase> loaded data
	 */
	public ArrayList<BlockBase> loadByName(String programName) {
		return loadByName(programName, false);
	}
	
	public ArrayList<BlockBase> loadByName(String programName, boolean isSample) {
		String selection = DBOpenHelper.SAVED_PROGRAM_ID + " = ?";
		String[] selectionArgs = {
			getProgramIdByName(programName, isSample) + ""
		};
		if (programName.equals(null)) {
			selection = null;
			selectionArgs = null;
		}
		Cursor c = mDb.query(DBOpenHelper.TBL_SAVED_PROGRAM_DATA,
				new String[] {
					DBOpenHelper.BLOCK_NAME,
					DBOpenHelper.BLOCK_LEFT,
					DBOpenHelper.BLOCK_TOP,
					DBOpenHelper.BLOCK_RIGHT,
					DBOpenHelper.BLOCK_BOTTOM,
					DBOpenHelper.BLOCK_NUM,
				}, selection, selectionArgs, null, null, null);
		
		return loadBlocks(c);
	}
	
	/**
	 * Load all not sample program names
	 * 
	 * @return
	 */
	public String[] loadSavedProgramNames() {
		return loadSavedProgramNames(false);
	}
	
	/**
	 * Load all program names (either sample or not sample)
	 * 
	 * @param isSample
	 * @return
	 */
	public String[] loadSavedProgramNames(boolean isSample) {
		String selection = DBOpenHelper.IS_SAMPLE + " = ?";
		String[] selectionArgs = {
			isSample ? "1" : "0"
		};
		String orderBy = DBOpenHelper.PROGRAM_NAME + " ASC";
		Cursor c = mDb.query(DBOpenHelper.TBL_SAVED_PROGRAMS,
				new String[] {
					DBOpenHelper.PROGRAM_NAME,
				}, selection, selectionArgs, null, null, orderBy);
		c.moveToFirst();
		int numRows = c.getCount();
		String[] programNames = new String[numRows];
		for (int i = 0; i < numRows; i++) {
			programNames[i] = c.getString(0);
			c.moveToNext();
		}
		c.close();
		return programNames;
	}
	
	/**
	 * Delete all Block data
	 */
	public void deleteAll() {
		mDb.delete(DBOpenHelper.TBL_PROGRAM_DATA, null, null);
	}
	
	public void deleteProgramByName(String programName) {
		deleteProgramByName(programName, false);
	}
	
	public void deleteProgramByName(String programName, boolean isSample) {
		int id = getProgramIdByName(programName, isSample);
		{
			String whereClause = DBOpenHelper.SAVED_PROGRAM_ID + " = ?";
			String[] whereClauseConditions = {
				id + ""
			};
			mDb.delete(DBOpenHelper.TBL_SAVED_PROGRAM_DATA, whereClause, whereClauseConditions);
		}
		{
			String whereClause = DBOpenHelper.PROGRAM_NAME + " = ?";
			String[] whereClauseConditions = {
				programName
			};
			mDb.delete(DBOpenHelper.TBL_SAVED_PROGRAMS, whereClause, whereClauseConditions);
		}
	}
	
}