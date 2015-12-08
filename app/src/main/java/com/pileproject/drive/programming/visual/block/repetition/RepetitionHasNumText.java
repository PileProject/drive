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

package com.pileproject.drive.programming.visual.block.repetition;

import android.content.Context;
import android.widget.TextView;

import com.pileproject.drive.programming.visual.block.NumTextHolder;


public abstract class RepetitionHasNumText extends RepetitionBlock implements NumTextHolder {

    protected TextView numText;

    /**
     * Constructor
     *
     * @param context The context of activity that creates this view
     */
    public RepetitionHasNumText(Context context) {
        super(context);
    }

    @Override
    public TextView getTextView() {
        return numText;
    }
}