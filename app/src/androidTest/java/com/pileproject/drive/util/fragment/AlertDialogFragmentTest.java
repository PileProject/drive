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

package com.pileproject.drive.util.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AlertDialog;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlertDialogFragmentTest {

    private TestActivity mActivity;

    private static final String TAG = "tag";

    @Rule
    public ActivityTestRule<TestActivity> mActivityRule = new ActivityTestRule<>(TestActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void testCheckComponent_titleString() {
        //mActivity.showDialog(new AlertDialogFragment.Builder(mActivity).setTag(TAG));

        AlertDialogFragment fragment = (AlertDialogFragment) mActivity
                .getSupportFragmentManager()
                .findFragmentByTag(TAG);

        assertNotNull(fragment);

        AlertDialog dialog = (AlertDialog) fragment.getDialog();

        assertNotNull(dialog);

        Button dialogButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        assertEquals(dialogButton.getText(), "OK");
    }

}
