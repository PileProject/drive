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

package com.pileproject.drive.util.string;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ParseUtil {

    private ParseUtil() {
        throw new AssertionError("This class cannot be instantiated");
    }

    /**
     * Parse a string as double in the context of default locale
     * (e.g., "1,234" means 1234E-3 in France).
     *
     * @throws NumberFormatException throw if the string makes no sense as number.
     */
    public static double doubleValueOf(String value) {
        return doubleValueOf(value, Locale.getDefault());
    }

    /**
     * Parse a string as double.
     * This function takes locale into account (e.g., "1,234" means 1234E-3 in France).
     *
     * @throws NumberFormatException throw if the string makes no sense as number.
     */
    public static double doubleValueOf(String value, Locale locale) throws NumberFormatException {
        try {
            NumberFormat format = NumberFormat.getNumberInstance(locale);
            Number number = format.parse(value);
            return number.doubleValue();
        } catch (ParseException e) {
            throw new NumberFormatException("String '" + value + "' cannot be parsed as double value" +
                    " in locale " + locale);
        }
    }

    /**
     * Parse a string as {@link BigDecimal} in the context of default locale
     * (e.g., "1,234" means 1234E-3 in France).
     *
     * @throws NumberFormatException throw if the string makes no sense as number.
     */
    public static BigDecimal bigDecimalValueOf(String value) throws NumberFormatException {
        return bigDecimalValueOf(value, Locale.getDefault());
    }

    /**
     * Parse a string as {@link BigDecimal}.
     * This function takes locale into account (e.g., "1,234" means 1234E-3 in France).
     *
     * @throws NumberFormatException throw if the string makes no sense as number.
     */
    public static BigDecimal bigDecimalValueOf(String value, Locale locale) throws NumberFormatException {
        try {
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
            decimalFormat.setParseBigDecimal(true);
            Number number = decimalFormat.parse(value);
            return (BigDecimal) number;
        } catch (ParseException e) {
            throw new NumberFormatException("String '" + value + "' cannot be parsed as double value" +
                    " in locale " + locale);
        }
    }
}
