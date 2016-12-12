/**
 * Copyright (C) 2011-2016 PILE Project, Inc. <dev@pileproject.com>
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
package com.pileproject.drive.programming.visual.block.sequence.car;

import android.content.Context;

import com.pileproject.drive.R;
import com.pileproject.drive.machine.CarControllerBase;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.machine.MachineController;
import com.pileproject.drive.programming.visual.block.sequence.SequenceBlockHasNumberText;
import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * Backward for a while
 *
 * @author yusaku
 * @version 1.0 7-July-2013
 */
public class BackwardSecBlock extends SequenceBlockHasNumberText {

    // TODO: set from preference
    private static final Range<BigDecimal> range = Range.closed(BigDecimal.ZERO, new BigDecimal(3));

    // TODO: set from preference
    private static final int PRECISION = 3;

    public BackwardSecBlock(Context context) {
        super(context, R.layout.block_backward_sec, R.id.block_numText);
    }

    @Override
    public int getPrecision() {
        return PRECISION;
    }

    @Override
    public Range<BigDecimal> getRange() {
        return range;
    }

    @Override
    public int action(MachineController controller, ExecutionCondition condition) {
        ((CarControllerBase) controller).moveBackward();
        return getActionValue();
    }

    @Override
    public Unit getUnit() {
        return Unit.Second;
    }
}
