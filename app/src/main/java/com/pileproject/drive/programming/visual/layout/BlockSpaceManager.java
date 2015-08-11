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

package com.pileproject.drive.programming.visual.layout;

import android.content.Context;
import android.view.View;

import com.pileproject.drive.database.DBManager;
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
	private DBManager mManager;
	
	public BlockSpaceManager(Context context, BlockSpaceLayout layout) {
		mContext = context;
		mLayout = layout;
		
		mManager = new DBManager(context);
	}
	
	public void save() {
		mManager.saveAllBlocks(mLayout);
	}

	public void saveWithName(String programName) {
		saveWithName(programName, false);
	}

	public void saveWithName(String programName, boolean isSample) {
		mManager.saveWithName(programName, mLayout, isSample);
	}
		
	public String saveAsNew() {
		String newName = mManager.createNewProgramName();
		mManager.saveWithName(newName, mLayout);
		return newName;
	}
	
	public void load() {
		ArrayList<BlockBase> data = mManager.loadAll();
		placeBlocks(data);
	}

	public void loadByName(String programName) {
		loadByName(programName, false);
	}
	
	public void loadByName(String programName, boolean isSample) {
		ArrayList<BlockBase> data = mManager.loadByName(programName, isSample);
		placeBlocks(data);
	}

	public String[] loadSavedProgramNames() {
		return mManager.loadSavedProgramNames();
	}
	
	public String[] loadSavedSampleProgramNames() {
		return mManager.loadSavedProgramNames(true);
	}
	
	public abstract void addBlocks(ArrayList<BlockBase> blocks);
	
	private void placeBlocks(ArrayList<BlockBase> data) {
		if (!data.isEmpty()) {
			addBlocks(data);
		}
		
		// Move all Views to old positions
		for (int i = 0; i < mLayout.getChildCount(); i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof BlockBase) {
				BlockBase b = (BlockBase) view;
				b.layout(b.left, b.top, b.right, b.bottom);
			}
		}
	}
	
	public void deleteAllBlocks() {
		mLayout.removeAllViews();
		mManager.deleteAll();
	}
}
