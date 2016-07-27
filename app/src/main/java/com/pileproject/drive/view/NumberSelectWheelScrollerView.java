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
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.math.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * a view which has a set of NumberPickers to select multiple-digits value
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public class NumberSelectWheelScrollerView extends NumberSelectViewBase {
    private List<NumberPicker> mNumbers;
    private int mIntegralPart, mDecimalPart;

    /**
     * @param context      an application context
     * @param integralPart the length of integral part
     * @param decimalPart  the length of decimal part
     */
    public NumberSelectWheelScrollerView(Context context, Range<Double> range, int integralPart, int decimalPart) {
        super(context, range);

        final double min = range.getLowerBound();
        final double max = range.getUpperBound();

        // choose somewhat correct lengths
        mIntegralPart = (integralPart > 0) ? integralPart : 3;
        mDecimalPart = (decimalPart >= 0) ? decimalPart : 0;
        mNumbers = new ArrayList<>(mIntegralPart + mDecimalPart);

        // create LayoutParams (width = 0, height = wrap_content, weight = 1.0f)
        LayoutParams lp = new LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;

        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            NumberPicker picker = new NumberPicker(context);

            // allow numbers 0 to 9
            picker.setMaxValue(9);
            picker.setMinValue(0);

            // hide software keyboard
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            picker.setOnValueChangedListener(new OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    int index = mNumbers.indexOf(picker);
                    picker.setValue(oldVal); // reset old values for check

                    double raw = getSelectedNum();

                    // prevent overflow
                    if ((oldVal + 1) % 10 == newVal) {
                        // check the new value is larger than the maximum value or not
                        if (raw + Math.pow(10, mIntegralPart - (index + 1)) > max) {
                            changeToMax(); // change value to maximum
                            return;
                        }
                    }
                    // prevent underflow
                    else {
                        // check the new value is lower than the minimum value or not
                        if (raw - Math.pow(10, mIntegralPart - (index + 1)) < min) {
                            changeToMin(); // change value to minimum
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
            // add period between integral part and decimal part
            TextView period = new TextView(context);
            period.setText(R.string.select_number_period);
            // period.setTextSize(R.dimen.period);

            lp.gravity = Gravity.CENTER;
            this.addView(period, mIntegralPart, lp);
        }
    }

    /**
     * carry up
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
     * carry down
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

    @Override
    public double getSelectedNum() {
        double result = 0;
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++)
            result += mNumbers.get(i).getValue() * Math.pow(10, mIntegralPart - (i + 1));
        return result;
    }

    @Override
    public void setNum(int num) {
        // set the number to NumberPicker
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            int digit = (int) Math.pow(10, mIntegralPart + mDecimalPart - (i + 1));
            mNumbers.get(i).setValue(num / digit);
            num %= digit;
        }
    }

    private void changeTo(double target) {
        int num = (int) (target * Math.pow(10, mDecimalPart));
        for (int i = 0; i < mIntegralPart + mDecimalPart; i++) {
            int digit = (int) Math.pow(10, mIntegralPart + mDecimalPart - (i + 1));
            mNumbers.get(i).setValue(num / digit);
            num %= digit;
        }
    }

    /**
     * change the number of this view to maximum value
     */
    public void changeToMax() {
        final double max = mRange.getUpperBound();
        changeTo(max);
    }

    /**
     * change the number of this view to minimum value
     */
    public void changeToMin() {
        final double min = mRange.getLowerBound();
        changeTo(min);
    }
}
