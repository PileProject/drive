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
package com.pileproject.drive.util.string;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ParseUtilTest {

    @Test
    public void testDoubleValueOf_whenLocaleIsJapan_thenRegardsDotAsRadixPoint() throws Exception {

        // there's no error when a double value represents 1.250 so let delta be 0.0
        assertEquals(1.250, ParseUtil.doubleValueOf("1.250", Locale.JAPAN), 0.0);
    }

    @Test
    public void testDoubleValueOf_whenLocaleIsEnglish_thenRegardsDotAsRadixPoint() throws Exception {

        assertEquals(1.250, ParseUtil.doubleValueOf("1.250", Locale.ENGLISH), 0.0);
    }

    @Test
    public void testDoubleValueOf_whenLocaleIsFrance_thenRegardsCommaAsRadixPoint() throws Exception {

        assertEquals(1.250, ParseUtil.doubleValueOf("1,250", Locale.FRANCE), 0.0);
    }

    @Test
    public void testDoubleValueOf_whenLocaleIsJapan_thenRegardsCommaAsSeparator() throws Exception {

        assertEquals(1_250, ParseUtil.doubleValueOf("1,250", Locale.JAPAN), 0.0);
    }

    @Test(expected = NumberFormatException.class)
    public void testDoubleValueOf_whenStringIsNotNumber_thenThrowsException() throws Exception {
        ParseUtil.doubleValueOf("foobar");
    }

    @Test
    public void testBigDecimalValueOf_whenLocaleIsJapan_thenRegardsDotAsRadixPoint() throws Exception {

        assertEquals(new BigDecimal("1.234"), ParseUtil.bigDecimalValueOf("1.234", Locale.JAPAN));
    }

    @Test
    public void testBigDecimalValueOf_whenLocaleIsEnglish_thenRegardsDotAsRadixPoint() throws Exception {

        assertEquals(new BigDecimal("1.234"), ParseUtil.bigDecimalValueOf("1.234", Locale.ENGLISH));
    }

    @Test
    public void testBigDecimalValueOf_whenLocaleIsFrance_thenRegardsCommaAsRadixPoint() throws Exception {

        assertEquals(new BigDecimal("1.234"), ParseUtil.bigDecimalValueOf("1,234", Locale.FRANCE));
    }

    @Test
    public void testBigDecimalValueOf_whenLocaleIsJapan_thenRegardsCommaAsSeparator() throws Exception {

        assertEquals(new BigDecimal(1_234), ParseUtil.bigDecimalValueOf("1,234", Locale.JAPAN));
    }

    @Test(expected = NumberFormatException.class)
    public void testBigDecimalValueOf_whenStringIsNotNumber_thenThrowsException() throws Exception {
        ParseUtil.bigDecimalValueOf("foobar");
    }
}