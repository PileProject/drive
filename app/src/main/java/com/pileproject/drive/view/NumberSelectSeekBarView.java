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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.Range;
import com.pileproject.drive.util.Unit;

/**
 * A view class which is used for number selecting on blocks
 * this class consists of one SeekBar and one TextView
 *
 * @author yusaku
 */
public class NumberSelectSeekBarView extends NumberSelectView {

    private final int mNumberOfIntegralDigits;
    private final int mNumberOfDecimalDigits;

    private final Unit mUnit;

    private double mValue;

    private SeekBar mSeekBar;
    private TextView mTextView;
    private final SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekbar, int value, boolean fromUser) {
            // the value should be jacked up by the apparent minimum value of SeekBar
            // when the value is displayed
            mValue = toDoubleExpression(value) + mRange.getLowerBound();

            final String fmt = "%" + mNumberOfIntegralDigits + "." + mNumberOfDecimalDigits + "f";

            // use the proper unit based on the value
            mTextView.setText(Unit.getUnitString(mContext, mUnit, fmt, mValue));
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // not used
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            // not used
        }
    };

    /**
     * @param context
     * @param range               - a range users can select within this range
     * @param unit                - a unit of the value in this view
     * @param numOfIntegralDigits - number of integral digits
     * @param numOfDecimalDigis   - number of decimal digits
     */
    public NumberSelectSeekBarView(Context context, Range<Double> range, Unit unit, int numOfIntegralDigits, int numOfDecimalDigis) {
        super(context, range);

        mUnit = unit;

        mNumberOfIntegralDigits = numOfIntegralDigits;
        mNumberOfDecimalDigits = numOfDecimalDigis;

        View layout = LayoutInflater.from(context).inflate(R.layout.view_number_select_seekbar, this);

        mTextView = (TextView) layout.findViewById(R.id.programming_numberSelectView_valueText);
        mSeekBar = (SeekBar) layout.findViewById(R.id.programming_numberSelectView_seekBar);

        mSeekBar.setOnSeekBarChangeListener(mListener);

        int min = toIntegerExpression(mRange.getLowerBound());
        int max = toIntegerExpression(mRange.getUpperBound());

        mSeekBar.setMax(max - min);
    }

    @Override
    public void setNum(int num) {
        double value = toDoubleExpression(num);

        if (!mRange.contains(value)) {
            throw new RuntimeException("The value to set SeekBar is out of range " +
                    "(range: " + mRange + ", value: " + value + ")");
        }

        // because minimum value of SeekBar is always 0,
        // the value to set SeekBar should be subtracted by its "apparent" minimum
        mValue = value - mRange.getLowerBound();

        mSeekBar.setProgress(toIntegerExpression(mValue));
    }

    @Override
    public double getSelectedNum() {
        return mValue;
    }

    /**
     * get rid of the decimal part
     *
     * @param value
     * @return
     */
    private int toIntegerExpression(double value) {
        return (int) (value * Math.pow(10.0, mNumberOfDecimalDigits));
    }

    private double toDoubleExpression(int value) {
        return value / Math.pow(10.0, mNumberOfDecimalDigits);
    }


}
