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

import android.view.View;

import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;


/**
 * EventBase for the move of block
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public class MoveEvent extends EventBase {
    int mOldX, mOldY;

    public MoveEvent(int elementCount, int index, int oldX, int oldY) {
        super(elementCount, index);

        mOldX = oldX;
        mOldY = oldY;
    }

    @Override
    public EventBase undo(BlockSpaceLayout layout, int elementCount) {
        View view = layout.getChildAt(mIndex); // Get view

        // Get current position of View and move
        int curX = view.getLeft();
        int curY = view.getTop();
        view.layout(mOldX,
                    mOldY,
                    mOldX + view.getWidth(),
                    mOldY + view.getHeight());

        // Create New MoveEvent for Redo
        EventBase diffForRedo = new MoveEvent(elementCount, mIndex, curX, curY);

        return diffForRedo;
    }
}