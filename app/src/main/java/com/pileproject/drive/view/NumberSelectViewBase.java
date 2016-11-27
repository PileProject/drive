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
package com.pileproject.drive.view;

import android.content.Context;
import android.widget.LinearLayout;

import java.math.BigDecimal;

/**
 * An abstract view class which provides an interface of a view for selecting values.
 */
public abstract class NumberSelectViewBase extends LinearLayout {

    public NumberSelectViewBase(Context context) {
        super(context);
    }

    /**
     * A getter of a value.
     * This is used to get the real value in double.
     * @return the selected value
     */
    public abstract BigDecimal getValue();

    /**
     * A setter of a value.
     * @param value a value to be set
     */
    public abstract void setValue(BigDecimal value);
}
