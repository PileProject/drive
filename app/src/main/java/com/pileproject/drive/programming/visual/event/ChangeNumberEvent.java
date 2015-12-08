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

package com.pileproject.drive.programming.visual.event;

import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumTextHolder;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;

/**
 * EventBase for the number change of TextView attached to block
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public class ChangeNumberEvent extends EventBase {
    private int mOldNum;

    public ChangeNumberEvent(int elementCount, int index, int oldNum) {
        super(elementCount, index);

        mOldNum = oldNum;
    }

    @Override
    public EventBase undo(BlockSpaceLayout layout, int elementCount) {
        BlockBase block = (BlockBase) layout.getChildAt(mIndex);

        // and then repeat undo and redo)
        // Get current number of TextView
        int curNum = ((NumTextHolder) block).getNum();
        ((NumTextHolder) block).setNum(mOldNum); // Set old number to TextView

        // Create a new ChangeNumberEvent for Redox
        EventBase diffForRedo = new ChangeNumberEvent(elementCount, mIndex, curNum);

        return diffForRedo;
    }
}