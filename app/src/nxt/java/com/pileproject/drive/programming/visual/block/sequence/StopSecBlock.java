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

import com.pileproject.drive.R;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.MachineController;
import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * Stop for a while
 *
 * @author yusaku
 * @version 1.0 7-July-2013
 */
public class StopSecBlock extends SequenceBlockHasNumberText {

    // TODO: set from preference
    private static final Range<BigDecimal> range = Range.closed(BigDecimal.ZERO, new BigDecimal(3));

    // TODO: set from preference
    private static final int PRECISION = 3;

    public StopSecBlock(Context context) {
        super(context, R.layout.block_stop_sec, R.id.block_numText);
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
        controller.halt();
        return getActionValue();
    }

    @Override
    public Unit getUnit() {
        return Unit.Second;
    }
}
