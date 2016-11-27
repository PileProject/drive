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

import com.pileproject.drive.util.development.Unit;
import com.pileproject.drive.util.math.Range;

import java.math.BigDecimal;

/**
 * Interface for block has TextView
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public interface NumberTextHolder {
    /**
     * Get value of the TextView
     */
    BigDecimal getValue();

    /**
     * Set value to TextView
     */
    void setValue(BigDecimal value);

    /**
     * Represents the precision of the value this class holds in 10-base.
     * Precision of a value means the length of decimal digits
     * e.g., if returns 3, the value can be set at 0.001-basis
     */
    int getPrecision();

    /**
     * Returns the range which users can set the value within
     * @return
     */
    Range<BigDecimal> getRange();

    /**
     * Returns unit of the value
     */
    Unit getUnit();

    /**
     * Enable the TextView in this holder if <code>enable</code> is <code>true</code>.
     * Disable the TextView in this holder if <code>enable</code> is <code>false</code>
     */
    void enableTextView(boolean enabled);

    /**
     * Set {@link android.view.View.OnLongClickListener} to the TextView
     */
    void setOnLongClickTextViewListener(View.OnLongClickListener listener);

    /**
     * Returns a string which represents the value in this view.
     * The string is formatted in {@link java.util.Locale#US} ("." means the radix point)
     */
    String getValueAsString();

    /**
     * Set the value as String.
     * The string should be formateed as {@link java.util.Locale#US}
     */
    void setValueAsString(String value);
}