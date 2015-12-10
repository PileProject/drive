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
    public static String getUnitString(
            Context context, Unit unit, String format, double value) {
        final String formattedValue = String.format(format, value);

        final int quantity = getQuantityOfValueInStringFormat(formattedValue, format);

        switch (unit) {
            case Second:
                return value + " " + context.getResources().getQuantityString(R.plurals.seconds, quantity);

            case Percentage:
                return value + " " + context.getResources().getString(R.string.percent);

            case NumberOfTimes:
                return context.getResources()
                        .getString(R.string.blocks_repeatNum,
                                   Integer.parseInt(formattedValue.trim()));

            default:
                break;
        }

        return "";
    }

    /**
     * returns a quantity of formattedValue
     * TODO: apply this for other locale than English
     * See also: http://www.unicode
     * .org/cldr/charts/latest/supplemental/language_plural_rules.html
     *
     * @param formattedValue
     * @param format
     * @return
     */
    private static int getQuantityOfValueInStringFormat(String formattedValue, String format) {
        if (formattedValue.equals(String.format(format, 1.0))) {
            return 1;
        }

        return 2;
    }
}
