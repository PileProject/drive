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

import jp.mity.drivecore.factory.sequence.option.SequenceBlockHasNumText;
import jp.mity.drivecore.utils.Unit;
import jp.mity.drivecore.execute.ExecuteCondition;
import jp.mity.drivecore.execute.DeviceController;
import jp.mity.nxtdrive.execute.NxtController;
import jp.mity.nxtdrive.execute.NxtController.MotorKind;
import jp.mity.nxtdrive.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/***
 * Set right motor power
 * 
 * @author yusaku
 * @version 1.0 7-July-2013
 */
public class SetRightMotorSpeedBlock extends SequenceBlockHasNumText {
	
	public SetRightMotorSpeedBlock(Context context) {
		super(context);
		View layout = LayoutInflater.from(context).inflate(R.layout.block_set_right_motor_speed, this);
		numText = (TextView) layout.findViewById(R.id.block_numText);
	}
	
	@Override
	public void setNum(int num) {
		numText.setText(String.valueOf(num));
	}
	
	@Override
	public int getNum() {
		return Integer.parseInt(numText.getText().toString());
	}
	
	@Override
	public Integer[] getDigit() {
		Integer[] digit = {
			3, 0
		};
		return digit;
	}
	
	@Override
	public double getMax() {
		return 100.0;
	}
	
	@Override
	public double getMin() {
		return 0.0;
	}
	
	@Override
	public int action(DeviceController controller, ExecuteCondition condition) {
		((NxtController) controller).setMotorPower(MotorKind.RightMotor, getNum());
		return 0;
	}
	
	@Override
	public Unit getUnit() {
		return Unit.Percentage;
	}
}
