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
import android.widget.SeekBar;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * A view class which is used for number selecting on blocks.
 * This class consists of one SeekBar and one TextView.
 */
public class NumberSelectSeekBarView extends NumberSelectViewBase {

    private final Range<BigDecimal> mRange;
    private final int mPrecision;
    private final SeekBar mSeekBar;

    private final Unit mUnit;
    private final TextView mTextView;
    private BigDecimal mValue;
    private final SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekbar, int value, boolean fromUser) {
            mValue = fromSeekBarExpression(value, mRange, mPrecision);

            mTextView.setText(mUnit.decorateValue(mValue, mPrecision));
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
     * Constructor.
     * @param context   context
     * @param value     initial value
     * @param range     BigDecimal range. users can select within this range
     * @param precision precision of the value
     * @param unit      unit of the value in this view
     */
    public NumberSelectSeekBarView(Context context, BigDecimal value, Range<BigDecimal> range,
                                   int precision, Unit unit) {
        super(context);

        mUnit = unit;
        mRange = range;
        mPrecision = precision;

        LayoutInflater.from(context).inflate(R.layout.view_number_select_seekbar, this);
        mTextView = (TextView) findViewById(R.id.programming_numberSelectView_valueText);
        mSeekBar = (SeekBar) findViewById(R.id.programming_numberSelectView_seekBar);

        mSeekBar.setOnSeekBarChangeListener(mListener);
        mSeekBar.setMax(toSeekBarExpression(mRange.getUpperBound(), mRange, precision));

        setValue(value);
    }

    private static int toSeekBarExpression(BigDecimal value, Range<BigDecimal> range, int precision) {

        if (!range.contains(value)) {
            return 0;
        }

        // if value = 0.2, range = (-1.0, 1.0), precision = 1, we want to get a result 12
        // because in seekbar expression, values in the range are converted as below
        // (-1.0, -0.9, ..., 0.0, ..., 0.9, 1.0) => (0, 1, ..., 10, ..., 19, 20)

        // so the following operations are needed:
        //   -1.0 => -10
        BigDecimal lowerMoved = range.getLowerBound().movePointRight(precision);
        //   0.2 => 2
        BigDecimal valueMoved = value.movePointRight(precision);
        //   2 - (-10) = 12
        BigDecimal ret = valueMoved.subtract(lowerMoved);

        return ret.intValue();
    }

    private static BigDecimal fromSeekBarExpression(int value, Range<BigDecimal> range, int precision) {

        BigDecimal valueMoved = new BigDecimal(value).movePointLeft(precision);
        BigDecimal ret = valueMoved.add(range.getLowerBound());

        if (!range.contains(ret)) {
            throw new RuntimeException("The value to set SeekBar is out of range " +
                    "(range: " + range + ", value: " + value + ")");
        }

        return ret;
    }

    @Override
    public BigDecimal getValue() {
        return mValue;
    }

    @Override
    public void setValue(BigDecimal value) {

        if (!mRange.contains(value)) {
            throw new RuntimeException("The value to set SeekBar is out of range " +
                                               "(range: " + mRange + ", value: " + value + ")");
        }

        mValue = value;
        mSeekBar.setProgress(toSeekBarExpression(mValue, mRange, mPrecision));
    }
}
