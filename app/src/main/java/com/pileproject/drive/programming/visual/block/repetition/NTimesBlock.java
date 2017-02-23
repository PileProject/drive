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
package com.pileproject.drive.programming.visual.block.repetition;

import android.content.Context;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.machine.MachineController;
import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * A block that represents a N times loop operation. To be more precise, this block represents the beginning of a loop
 * and the end is expressed by {@link RepetitionEndBlock}.
 */
public class NTimesBlock extends RepetitionBlockHasNumberText {

    // TODO: set from preference
    private static final Range<BigDecimal> range = Range.closed(BigDecimal.ONE, new BigDecimal(5));

    private static final int PRECISION = 0;

    public NTimesBlock(Context context) {
        super(context, R.layout.block_n_times, R.id.block_numText);
    }

    @Override
    public int action(MachineController controller, ExecutionCondition condition) {
        int n = getValue().intValue();
        condition.enterNTimesLoop(n);

        return 0;
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
    public Unit getUnit() {
        return Unit.NumberOfTimes;
    }

}
