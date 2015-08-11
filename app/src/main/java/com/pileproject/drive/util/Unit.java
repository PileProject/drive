/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.util;

import android.content.Context;

import com.pileproject.drive.R;

/**
 * enumeration of unit for number selecting on blocks
 *
 * @author yusaku
 */
public enum Unit {
    Second,
    Percentage,
    NumberOfTimes,
    Dimensionless;

    /**
     * get the unit string based on the value and selected unit
     *
     * @return string which contains the value and its unit
     */
    public static String getUnitString(Context context, Unit unit, String format, double value) {
        final String formatedValue = String.format(format, value);

        final int quantity = getQuantityOfValueInStringFormat(formatedValue, format);

        switch (unit) {
            case Second:
                return value + " " + context.getResources().getQuantityString(R.plurals.seconds, quantity);

            case Percentage:
                return value + " " + context.getResources().getString(R.string.percent);

            case NumberOfTimes:
                return context.getResources().getString(R.string.blocks_repeatNum, Integer.parseInt(formatedValue.trim()));

            default:
                break;
        }

        return "";
    }

    /**
     * returns a quantity of formatedValue
     * TODO: apply this for other locale than English
     * See also: http://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html
     *
     * @param formatedValue
     * @param format
     * @return
     */
    private static int getQuantityOfValueInStringFormat(String formatedValue, String format) {
        if (formatedValue.equals(String.format(format, 1.0))) {
            return 1;
        }

        return 2;
    }
}
