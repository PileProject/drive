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

package com.pileproject.drive.programming.visual.block;

import android.widget.TextView;

import com.pileproject.drive.util.development.Unit;

/**
 * Interface for block has TextView
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public interface NumTextHolder {
    /**
     * Get number as Integer from TextView
     *
     * @return int
     * Value of textView as integer
     */
    int getNum();

    /**
     * Set number to TextView
     *
     * @param num the number that is set to TextView
     */
    void setNum(int num);

    /**
     * Get TextView
     *
     * @return TextView
     */
    TextView getTextView();

    /**
     * Get Digit of the number of this block
     *
     * @return Integer[]
     * 0 - Integral Part, 1 - Decimal Part
     */
    Integer[] getDigit();

    /**
     * Get Max Value of this block
     *
     * @return double
     * Maximum value
     */
    double getMax();

    /**
     * Get Min Value of this block
     *
     * @return double
     * Minimum value
     */
    double getMin();

    /**
     * Returns unit of the value which is contained in this text field
     *
     * @return
     */
    Unit getUnit();
}