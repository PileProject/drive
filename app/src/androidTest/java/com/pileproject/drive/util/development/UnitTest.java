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

package com.pileproject.drive.util.development;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.pileproject.drive.app.DriveApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

@RunWith(Enclosed.class)
public class UnitTest {

    public static void setLocale(Locale locale) {

        Resources res = DriveApplication.getContext().getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @RunWith(AndroidJUnit4.class)
    public static class InUS {

        @Before
        public void before() {
            setLocale(Locale.US);
        }

        @Test
        public void whenUnitIsSecond_thenDecoratesAsSecondString() throws Exception {
            assertEquals("When value is 1.0 and unit is second",
                    "1.000s", Unit.Second.decorateValue(Locale.ENGLISH, BigDecimal.ONE, 3));
        }

        @Test
        public void whenUnitIsPercent_thenDecoratesAsPercentString() throws Exception {
            assertEquals("When value is 100 and unit is percentage",
                    "100%", Unit.Percentage.decorateValue(Locale.ENGLISH, new BigDecimal(100), 0));
        }

        @Test
        public void whenUnitIsNumberOfTimes_thenDecoratesAsNumberOfTimesString() throws Exception {
            assertEquals("When the value 100 and unit is NumberOfTimes",
                    "Repeats: 100", Unit.NumberOfTimes.decorateValue(Locale.ENGLISH, new BigDecimal(100), 0));
        }
    }

    @RunWith(AndroidJUnit4.class)
    public static class InJP {

        @Before
        public void before() {
            setLocale(Locale.JAPAN);
        }

        @Test
        public void whenUnitIsSecond_thenDecoratesAsSecondString() throws Exception {
            assertEquals("When value is 1.0 and unit is second",
                    "1.000びょう", Unit.Second.decorateValue(Locale.JAPAN, BigDecimal.ONE, 3));
        }

        @Test
        public void whenUnitIsPercent_thenDecoratesAsPercentString() throws Exception {
            assertEquals("When value is 100 and unit is percentage",
                    "100%", Unit.Percentage.decorateValue(Locale.JAPAN, new BigDecimal(100), 0));
        }

        @Test
        public void whenUnitIsNumberOfTimes_thenDecoratesAsNumberOfTimesString() throws Exception {
            assertEquals("When the value 100 and unit is NumberOfTimes",
                    "100 かい", Unit.NumberOfTimes.decorateValue(Locale.JAPAN, new BigDecimal(100), 0));
        }
    }

}