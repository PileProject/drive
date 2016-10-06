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

import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class NumberTextViewDelegateTest {

    private static final int PRECISION = 3;

    public TextView mockTextView;

    public NumberTextHolder mockNumberTextHolder;

    public NumberTextViewDelegate numberTextViewDelegate;

    @Before
    public void before() {
        mockTextView = Mockito.mock(TextView.class);

        mockNumberTextHolder = Mockito.mock(NumberTextHolder.class);

        doReturn(PRECISION).when(mockNumberTextHolder).getPrecision();

        numberTextViewDelegate = new NumberTextViewDelegate(mockTextView, mockNumberTextHolder);
    }

    @Test
    public void whenTextViewReturnsNumber_thenGetValueSucceeds() throws Exception {

        doReturn("2.500").when(mockTextView).getText();
        assertTrue(new BigDecimal("2.500").compareTo(numberTextViewDelegate.getValue()) == 0);
    }

    @Test
    public void whenTextViewReturnsNumber_thenGetActionValueSucceeds() throws Exception {

        doReturn("2.500").when(mockTextView).getText();
        assertEquals(2500, numberTextViewDelegate.getActionValue());
    }

    @Test
    public void whenTextViewDisabled_thenVerifyTheMethodsCalled() throws Exception {

        numberTextViewDelegate.enableTextView(false);

        verify(mockTextView).setFocusable(false);
        verify(mockTextView).setFocusableInTouchMode(false);
        verify(mockTextView).setEnabled(false);
    }
}