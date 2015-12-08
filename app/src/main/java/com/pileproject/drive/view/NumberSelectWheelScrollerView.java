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

package com.pileproject.drive.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.Range;

import java.util.ArrayList;

/**
 * A layout which has the set of NumberPickers
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public class NumberSelectWheelScrollerView extends NumberSelectView {

    private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private ArrayList<NumberPicker> mNumbers;
    private int mIntegralPart, mDecimalPart;

    /**
     * Constructor
     *
     * @param context      application context
     * @param integralPart the length of integral part
     * @param decimalPart  the length of decimal part
     */
    public NumberSelectWheelScrollerView(
            Context context, Range<Double> range, int integralPart, int decimalPart) {
        super(context, range);

        final double min = range.getLowerBound();
        final double max = range.getUpperBound();

        mIntegralPart = (integralPart > 0) ? integralPart : 3;
        mDecimalPart = (decimalPart >= 0) ? decimalPart : 0;
        mNumbers = new ArrayList<>(mIntegralPart + mDecimalPart);

        // Create LayoutParams (width = 0, height = wrap_content, weight = 1.0f)
        LayoutParams lp = new LayoutParams(0, WC);
        lp.weight = 1.0f;

        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            NumberPicker picker = new NumberPicker(context);

            picker.setMaxValue(9);
            picker.setMinValue(0);

            // Hide software keyboard
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            picker.setOnValueChangedListener(new OnValueChangeListener() {
                @Override
                public void onValueChange(
                        NumberPicker picker, int oldVal, int newVal) {

                    int index = mNumbers.indexOf(picker);
                    picker.setValue(oldVal); // for check
                    double raw = getSelectedNum();

                    // prevent overflow
                    if ((oldVal + 1) % 10 == newVal) {
                        // Check the new value is larger than the maximum value
                        // or not
                        if (raw + Math.pow(10, mIntegralPart - (index + 1)) > max) {
                            changeToMax(); // Change value to max.
                            return;
                        }
                    }
                    // prevent underflow
                    else {
                        // Check the new value is lower than the minimum
                        // value or not
                        if (raw - Math.pow(10, mIntegralPart - (index + 1)) < min) {
                            changeToMin(); // Change value to min.
                            return;
                        }
                    }
                    picker.setValue(newVal);
                }
            });
            this.addView(picker, lp);
            mNumbers.add(picker);
        }

        if (mDecimalPart != 0) {
            // Add period between integral part and decimal part
            TextView period = new TextView(context);
            period.setText(R.string.select_number_period);
            // period.setTextSize(R.dimen.period);
            lp.gravity = Gravity.CENTER;
            this.addView(period, mIntegralPart, lp);
        }
    }

    /**
     * Carry down the number
     *
     * @param index the index of the target number
     */
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
     * Carry down the number
     *
     * @param index the index of the target number
     */
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

    /**
     * Get selected raw value
     *
     * @return double
     * Raw value
     */
    @Override
    public double getSelectedNum() {
        double result = 0;
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            result += mNumbers.get(i).getValue() * Math.pow(10, mIntegralPart - (i + 1));
        }
        return result;
    }

    /**
     * Set the value to this view
     *
     * @param num the number to be set, scaled to integer
     */
    @Override
    public void setNum(int num) {
        // Set the number to NumberPicker
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            int digit = (int) Math.pow(10, mIntegralPart + mDecimalPart - (i + 1));
            mNumbers.get(i).setValue(num / digit);
            num %= digit;
        }
    }

    /**
     * Change the number of this view to max. value
     */
    public void changeToMax() {
        final double max = mRange.getUpperBound();

        int num = (int) (max * Math.pow(10, mDecimalPart));
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            int digit = (int) Math.pow(10, mIntegralPart + mDecimalPart - (i + 1));
            mNumbers.get(i).setValue(num / digit);
            num %= digit;
        }
    }

    /**
     * Change the number of this view to min. value
     */
    public void changeToMin() {
        final double min = mRange.getLowerBound();

        int num = (int) (min * Math.pow(10, mDecimalPart));
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            int digit = (int) Math.pow(10, mIntegralPart + mDecimalPart - (i + 1));
            mNumbers.get(i).setValue(num / digit);
            num %= digit;
        }
    }
}
