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

package com.pileproject.drive.programming.visual.block.selection;

import android.content.Context;
import android.widget.TextView;

import com.pileproject.drive.programming.visual.block.NumTextHolder;

/**
 * SelectionBlock that has a TextView
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public abstract class SelectionHasNumText extends SelectionBlock implements NumTextHolder {

    protected TextView numText;

    public SelectionHasNumText(Context context) {
        super(context);
    }

    @Override
    public TextView getTextView() {
        return numText;
    }
}