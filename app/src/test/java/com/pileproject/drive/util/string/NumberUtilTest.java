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

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.*;

public class NumberUtilTest {

    @Test
    public void whenLocaleIsJapan_thenConvertsAsJapaneseFormat() throws Exception {
        assertEquals("When the precision 3 and locale Japan",
                "1.250", NumberUtil.toString(Locale.JAPAN, 1.25, 3));
    }

    @Test
    public void whenLocaleIsFrance_thenConvertsAsFrenchFormat() throws Exception {
        assertEquals("When the precision 3 and locale France",
                "1,250", NumberUtil.toString(Locale.FRANCE, 1.25, 3));
    }

    @Test
    public void whenValueTypeIsBigDecimal_thenConvertsSuccessfully() throws Exception {
        assertEquals("When the precision 4 and locale Japan",
                "1.2345", NumberUtil.toString(Locale.JAPAN, new BigDecimal("1.2345"), 4));
    }
}