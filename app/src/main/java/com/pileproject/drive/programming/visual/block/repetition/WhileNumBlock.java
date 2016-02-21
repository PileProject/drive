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

package com.pileproject.drive.programming.visual.block.repetition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.util.Unit;

/**
 * While in selected times
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class WhileNumBlock extends RepetitionHasNumText {

    public WhileNumBlock(Context context) {
        super(context);

        View layout = LayoutInflater.from(context).inflate(R.layout.block_while_num, this);
        numText = (TextView) layout.findViewById(R.id.block_numText);
    }

    @Override
    public int getNum() {
        return Integer.parseInt(numText.getText().toString());
    }

    @Override
    public void setNum(int num) {
        numText.setText(String.valueOf(num));
    }

    @Override
    public Integer[] getDigit() {
        return new Integer[]{2, 0};
    }

    @Override
    public double getMax() {
        return (double) 5;
    }

    @Override
    public double getMin() {
        return (double) 1;
    }

    @Override
    public int action(
            MachineController controller, ExecutionCondition condition) {
        int index = condition.programCount;
        for (int i = 1; i < getNum(); i++) {
            condition.whileStack.push(index);
        }
        condition.beginningOfCurrentWhileLoop = index;
        return 0;
    }

    @Override
    public Unit getUnit() {
        return Unit.NumberOfTimes;
    }
}
