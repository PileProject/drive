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

package com.pileproject.drive.programming.visual.block.sequence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.execution.NxtController;
import com.pileproject.drive.util.Unit;

import java.util.Locale;

/**
 * Forward for a while
 *
 * @author yusaku
 * @version 1.0 7-July-2013
 */
public class ForwardSecBlock extends SequenceBlockHasNumText {

    public ForwardSecBlock(Context context) {
        super(context);
        // Create view defined as the layout XML by LayoutInflater
        // Set this view to LayoutInflater#inflate() 2nd argument
        View layout = LayoutInflater.from(context).inflate(R.layout.block_forward_sec, this);
        numText = (TextView) layout.findViewById(R.id.block_numText);
    }

    @Override
    public int getNum() {
        double raw = Double.parseDouble(numText.getText().toString());
        return (int) (raw * 1000);
    }

    @Override
    public void setNum(int num) {
        double raw = (double) num / 1000.0;
        numText.setText(String.format(Locale.ENGLISH, "%.3f", raw));
    }

    @Override
    public Integer[] getDigit() {
        return new Integer[]{1, 3};
    }

    @Override
    public double getMax() {
        return 3.0;
    }

    @Override
    public double getMin() {
        return 0.000;
    }

    @Override
    public int action(MachineController controller, ExecutionCondition condition) {
        ((NxtController) controller).moveForward();
        return getNum();
    }

    @Override
    public Unit getUnit() {
        return Unit.Second;
    }
}
