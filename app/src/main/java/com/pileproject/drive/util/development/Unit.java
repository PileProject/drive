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
package com.pileproject.drive.util.development;

import android.content.res.Resources;

import com.pileproject.drive.R;
import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.util.string.NumberUtil;

import java.util.Locale;

/**
 * An enumeration of unit for number selecting on blocks.
 */
public class Unit {

    public static final Unit Second = new Unit();
    public static final Unit Percentage = new Unit();
    public static final Unit NumberOfTimes = new Unit();

    private static final Resources RESOURCES = DriveApplication.getContext().getResources();

    private Unit() {

    }

    /**
     * Decorates numeric value with the {@link Unit} string with <code>precision</code>.
     * This method takes locales into account and uses default locale by calling
     * {@link Locale#getDefault()}.
     *
     * @param value a value to be decorated
     * @param precision the precision of the value in the return string
     * @param <T> the type parameter for <code>value</code>, which extends {@link Number}
     * @return a decorated string in default locale and with given precision
     */
    public <T extends Number> String decorateValue(T value, int precision) {
        return decorateValue(Locale.getDefault(), value, precision);
    }

    /**
     * Decorates numeric value with the {@link Unit} string with <code>precision</code>.
     * This method takes locales into account.
     *
     * @param locale the locale in which the value decorated
     * @param value a value to be decorated
     * @param precision the precision of the value in the return string
     * @param <T> the type parameter for <code>value</code>, which extends {@link Number}
     * @return a decorated string in default locale and with given precision
     */
    public <T extends Number> String decorateValue(Locale locale, T value, int precision) {

        if (this == Second) {
            return NumberUtil.toString(locale, value, precision) + RESOURCES.getString(R.string.second);
        }

        if (this == Percentage) {
            return NumberUtil.toString(locale, value, precision) + RESOURCES.getString(R.string.percent);
        }

        // if this == NumberOfTimes
        return RESOURCES.getString(R.string.blocks_repeatNum, value.intValue());
    }
}
