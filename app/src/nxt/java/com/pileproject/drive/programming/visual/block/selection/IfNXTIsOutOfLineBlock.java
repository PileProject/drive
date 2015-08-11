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

package com.pileproject.drive.programming.visual.block.selection;

import android.content.Context;
import android.view.LayoutInflater;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.DeviceController;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.util.SharedPreferencesWrapper;


/**
 * This block check the light sensor's value
 * If the value is higher than threshold, it means nxt is on the
 * light-colored floor and out of black line.
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class IfNXTIsOutOfLineBlock extends SelectionBlock {
	
	private int mThreshold;

	public IfNXTIsOutOfLineBlock(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.block_if_nxt_is_out_of_line, this);
		
		mThreshold = SharedPreferencesWrapper.loadIntPreference(context, IfNXTIsOutOfLineBlock.class.getName(), 50);
	}
	
	@Override
	public int action(DeviceController controller, ExecutionCondition condition) {
		// comment is weird.
		// getLightPercent returns tenfold value
		condition.pushSelectionResult(((NxtController) controller).lightSensor.getLightPercent() > mThreshold * 10);
		return 0;
	}
}
