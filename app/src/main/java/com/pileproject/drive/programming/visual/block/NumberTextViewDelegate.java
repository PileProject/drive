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
package com.pileproject.drive.programming.visual.block;

import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.util.string.NumberUtil;
import com.pileproject.drive.util.string.ParseUtil;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * This class represents a delegate for classes with a TextView.
 * Most of the methods in {@link NumberTextHolder} are implemented in this class.
 *
 * The list of methods in {@link NumberTextHolder} which are not implemented is:
 * <ul>
 *     <li>{@link NumberTextHolder#getPrecision()}</li>
 *     <li>{@link NumberTextHolder#getRange()}</li>
 *     <li>{@link NumberTextHolder#getUnit()}</li>
 * </ul>
 *
 * These methods should be implemented in concrete classes.
 */
public class NumberTextViewDelegate {

    private final TextView textView;
    private final int precision;

    private BigDecimal valueCache;

    public NumberTextViewDelegate(TextView textView, NumberTextHolder holder) {
        this.precision = holder.getPrecision();
        this.textView = textView;
    }

    public int getActionValue() {
        return getValue().movePointRight(precision).intValue();
    }

    public BigDecimal getValue() {
        if (valueCache == null) {
            String value = (String) textView.getText();
            valueCache = ParseUtil.bigDecimalValueOf(value);
        }

        return valueCache;
    }

    public void setValue(BigDecimal value) {
        valueCache = value;
        textView.setText(NumberUtil.toString(value, precision));
    }

    public String getValueAsString() {
        return getValue().toString();
    }

    public void setValueAsString(String value) {
        setValue(ParseUtil.bigDecimalValueOf(value, Locale.US));
    }

    public void enableTextView(boolean enabled) {
        textView.setFocusable(enabled);
        textView.setFocusableInTouchMode(enabled);
        textView.setEnabled(enabled);
    }

    public void setOnTextViewLongClickListener(View.OnLongClickListener listener) {
        textView.setOnLongClickListener(listener);
    }
}
