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
package com.pileproject.drive.programming.visual.block;

import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * An interface for blocks which have {@link TextView}.
 */
public interface NumberTextHolder {
    /**
     * Gets value of the TextView.
     *
     * @return the {@link BigDecimal}
     */
    BigDecimal getValue();

    /**
     * Sets the value to the {@link TextView}.
     *
     * @param value a value to be set on the {@link TextView}
     */
    void setValue(BigDecimal value);

    /**
     * Represents the precision of the value this class holds in 10-base.
     * Precision of a value means the length of decimal digits.
     * For example, if returns 3, the value can be set at 0.001-basis.
     *
     * @return the precision of the value
     */
    int getPrecision();

    /**
     * Returns the range which users can set the value within.
     *
     * @return the {@link Range}
     */
    Range<BigDecimal> getRange();

    /**
     * Returns unit of the value.
     *
     * @return the {@link Unit}
     */
    Unit getUnit();

    /**
     * Enables the {@link TextView} in this holder if <code>enable</code> is <code>true</code>.
     * Disables the {@link TextView} in this holder if <code>enable</code> is <code>false</code>.
     *
     * @param enabled allow to edit the {@link TextView} (<code>true</code>) or not (<code>false</code>)
     */
    void enableTextView(boolean enabled);

    /**
     * Sets {@link android.view.View.OnLongClickListener} to the {@link TextView}.
     *
     * @param listener
     * a {@link android.view.View.OnLongClickListener} to be called when a user edits the value on {@link TextView}.
     */
    void setOnLongClickTextViewListener(View.OnLongClickListener listener);

    /**
     * Returns a string which represents the value in this view.
     * The string is formatted in {@link java.util.Locale#US} ("." means the radix point).
     *
     * @return a formatted string
     */
    String getValueAsString();

    /**
     * Sets the value as String.
     * The string should be formatted as {@link java.util.Locale#US}.
     *
     * @param value a formatted string
     */
    void setValueAsString(String value);
}