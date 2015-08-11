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

import java.util.Locale;

/**
 * A class which detects which measurement unit is used in specified country
 * <p/>
 * Now, however, this class is only for the United States of America,
 * the only major country where people use Imperial units
 *
 * @author Yusaku
 */
public class MeasurementUnit {
    public static MeasurementUnit Imperial = new MeasurementUnit();
    public static MeasurementUnit Metric = new MeasurementUnit();

    public static MeasurementUnit getDefaultUnit() {
        return getFromLocale(Locale.getDefault());
    }

    public static MeasurementUnit getFromLocale(Locale locale) {
        String countryCode = locale.getCountry();
        if ("US".equals(countryCode)) {
            return Imperial;
        }

        return Metric;
    }

    public static double convertCmToInch(double cm) {
        return cm / 2.54;
    }
}
