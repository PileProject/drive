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

import android.content.Context;
import android.widget.RelativeLayout;

import com.pileproject.drive.execution.ExecutionCondition;
import com.pileproject.drive.execution.MachineController;

/**
 * BaseBlock
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public abstract class BlockBase extends RelativeLayout {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public BlockBase(Context context) {
        super(context);
    }

    /**
     * Action that this block does while the execution of program.
     * Return delay that occurs after this action
     * on the millisecond time scale.
     *
     * @param controller Controller of Device
     * @param condition  Condition of the executing program
     * @return
     */
    public abstract int action(
            MachineController controller, ExecutionCondition condition);

    /**
     * Return class to check a program is correct or not
     *
     * @return Class<? extends BaseBlock>
     */
    public abstract Class<? extends BlockBase> getKind();
}