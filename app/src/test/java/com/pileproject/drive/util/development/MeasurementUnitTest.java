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
package com.pileproject.drive.util.development;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class MeasurementUnitTest {
    @Test
    public void getFromLocaleTest() throws Exception {
        assertEquals(MeasurementUnit.getFromLocale(Locale.US), MeasurementUnit.Imperial);

        assertEquals(MeasurementUnit.getFromLocale(Locale.JAPAN), MeasurementUnit.Metric);
        assertEquals(MeasurementUnit.getFromLocale(Locale.FRANCE), MeasurementUnit.Metric);
    }
}