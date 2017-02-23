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
package com.pileproject.drive.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A view which has a set of {@link NumberPicker}s to select multiple-digits value.
 */
public class NumberSelectWheelScrollerView extends NumberSelectViewBase {
    private List<NumberPicker> mNumbers;

    private final Range<BigDecimal> mRange;
    private final int mScaledMaximum;
    private final int mScaledMinimum;

    private final int mDigitCounts;
    private final int mPrecision;

    private final OnValueChangeListener listener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            picker.setValue(newVal);

            int scaledValue = getScaledValue();

            if (scaledValue > mScaledMaximum) {
                changeToMax();
            } else if (scaledValue < mScaledMinimum) {
                changeToMin();
            }
        }
    };

    /**
     * @param context the context of the {@link Activity} which shows this view
     * @param value the initial value on the view
     * @param range a {@link BigDecimal} range where users can select the value
     * @param precision the precision of the value
     * @param digitCount the number of digits this view has
     */
    public NumberSelectWheelScrollerView(Context context, BigDecimal value, Range<BigDecimal> range,
                                         int precision, int digitCount) {
        super(context);

        mPrecision = precision;
        mDigitCounts = digitCount;

        mRange = range;
        mScaledMaximum = mRange.getUpperBound().movePointRight(mPrecision).intValue();
        mScaledMinimum = mRange.getLowerBound().movePointRight(mPrecision).intValue();

        mNumbers = new ArrayList<>(mDigitCounts);

        // create LayoutParams (width = 0, height = wrap_content, weight = 1.0f)
        LayoutParams lp = new LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;

        for (int i = 0; i < mDigitCounts; i++) {
            NumberPicker picker = createNumberPicker(context);

            this.addView(picker, lp);
            mNumbers.add(picker);
        }

        if (mPrecision != 0) {
            // add period between integral part and decimal part
            TextView period = new TextView(context);
            period.setText(R.string.select_number_period);
            // period.setTextSize(R.dimen.period);

            lp.gravity = Gravity.CENTER;
            int insertIndex = mDigitCounts - mPrecision;
            this.addView(period, insertIndex, lp);
        }

        setValue(value);
    }

    @Override
    public BigDecimal getValue() {
        return new BigDecimal(getScaledValue()).movePointLeft(mPrecision);
    }

    @Override
    public void setValue(BigDecimal value) {
        setScaledValue(value.movePointRight(mPrecision).intValue());
    }

    /**
     * Changes the number of this view to maximum value.
     */
    public void changeToMax() {
        setScaledValue(mScaledMaximum);
    }

    /**
     * Changes the number of this view to minimum value.
     */
    public void changeToMin() {
        setScaledValue(mScaledMinimum);
    }

    private NumberPicker createNumberPicker(Context context) {

        NumberPicker picker = new NumberPicker(context);

        // allow numbers 0 to 9
        picker.setMaxValue(9);
        picker.setMinValue(0);

        // hide software keyboard
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setOnValueChangedListener(listener);

        return picker;
    }

    private int getScaledValue() {
        int scaledResult = 0;

        for (int i = 0; i < mDigitCounts; ++i) {
            int digit = mNumbers.get(mDigitCounts - (i + 1)).getValue();

            scaledResult += digit * Math.pow(10, i);
        }

        return scaledResult;
    }

    private void setScaledValue(int scaledValue) {
        for (int i = 0; i < mDigitCounts; ++i) {
            int digit = (int) Math.pow(10, mDigitCounts - (i + 1));
            mNumbers.get(i).setValue(scaledValue / digit);
            scaledValue %= digit;
        }
    }

    /**
     * Carries up the count.
     *
     * @param index the index of the target number
     */
    @Deprecated
    public void carryUp(int index) {
        if (index < 0) {
            return;
        }

        NumberPicker picker = mNumbers.get(index);
        int oldVal = picker.getValue();
        if (oldVal == 9) {
            picker.setValue(0);
            carryUp(index - 1);
        } else {
            picker.setValue(picker.getValue() + 1);
        }
    }

    /**
     * Carries down the count.
     *
     * @param index the index of the target number
     */
    @Deprecated
    public void carryDown(int index) {
        if (index < 0) {
            return;
        }

        NumberPicker picker = mNumbers.get(index);
        int oldVal = picker.getValue();
        if (oldVal == 0) {
            picker.setValue(9);
            carryDown(index - 1);
        } else {
            picker.setValue(picker.getValue() - 1);
        }
    }
}
