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
package com.pileproject.drive.programming.visual.block.repetition;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.programming.visual.block.NumberTextHolder;
import com.pileproject.drive.programming.visual.block.NumberTextViewDelegate;

import java.math.BigDecimal;

public abstract class RepetitionBlockHasNumberText extends RepetitionBlock implements NumberTextHolder {

    private final NumberTextViewDelegate mDelegate;

    /**
     * Constructor
     *
     * @param context The context of activity that creates this view
     */
    public RepetitionBlockHasNumberText(Context context, @LayoutRes int layoutRes, @IdRes int textViewId) {
        super(context, layoutRes);

        mDelegate = new NumberTextViewDelegate((TextView) findViewById(textViewId), this);
    }

    @Override
    public BigDecimal getValue() {
        return mDelegate.getValue();
    }

    @Override
    public void setValue(BigDecimal value) {
        mDelegate.setValue(value);
    }

    @Override
    public String getValueAsString() {
        return mDelegate.getValueAsString();
    }

    @Override
    public void setValueAsString(String value) {
        mDelegate.setValueAsString(value);
    }

    @Override
    public void enableTextView(boolean enabled) {
        mDelegate.enableTextView(enabled);
    }

    @Override
    public void setOnLongClickTextViewListener(View.OnLongClickListener listener) {
        mDelegate.setOnTextViewLongClickListener(listener);
    }
}