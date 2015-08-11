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

package com.pileproject.drive.programming.visual.block.sequence;

import java.util.Locale;

import jp.mity.drivecore.factory.sequence.option.SequenceBlockHasNumText;
import jp.mity.drivecore.utils.Unit;
import jp.mity.drivecore.execute.ExecuteCondition;
import jp.mity.drivecore.execute.DeviceController;
import jp.mity.nxtdrive.R;
import jp.mity.nxtdrive.execute.NxtController;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/***
 * Stop for a while
 * 
 * @author yusaku
 * @version 1.0 7-July-2013
 */
public class StopSecBlock extends SequenceBlockHasNumText {
	
	public StopSecBlock(Context context) {
		super(context);
		View layout = LayoutInflater.from(context).inflate(R.layout.block_stop_sec, this);
		numText = (TextView) layout.findViewById(R.id.block_numText);
	}
	
	@Override
	public void setNum(int num) {
		double raw = num / 1000.0;
		numText.setText(String.format(Locale.ENGLISH, "%.3f", raw));
	}
	
	@Override
	public int getNum() {
		double raw = Double.parseDouble(numText.getText().toString());
		return (int) (raw * 1000.0);
	}
	
	@Override
	public Integer[] getDigit() {
		Integer[] digit = {
			1, 3
		};
		return digit;
	}
	
	@Override
	public double getMax() {
		return 3.0;
	}
	
	@Override
	public double getMin() {
		return 0.0;
	}
	
	@Override
	public int action(DeviceController controller, ExecuteCondition condition) {
		((NxtController) controller).halt();
		return getNum();
	}
	
	@Override
	public Unit getUnit() {
		return Unit.Second;
	}
}
