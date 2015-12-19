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

import android.view.ViewGroup;

import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.BlockFactory;
import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;

/**
 * EventBase for the delete of a block
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public class DeleteEvent extends EventBase {
    private String mBlockName;

    public DeleteEvent(int elementCount, int index, String name) {
        super(elementCount, index);

        mBlockName = name;
    }

    @Override
    public EventBase undo(BlockSpaceLayout layout, int elementCount) {
        // Recreate block
        BlockBase block = BlockFactory.createBlocks(BlockFactory.UNDO, mBlockName).get(0);

        // Attach it to layout
        layout.addView(block, mIndex,
                       new BlockSpaceLayout.LayoutParams(
                               ViewGroup.LayoutParams.WRAP_CONTENT,
                               ViewGroup.LayoutParams.WRAP_CONTENT));

        // Create a new AddNumDiff for Redo
        EventBase diffForRedo = new AddEvent(elementCount, mIndex);
        return diffForRedo;
    }
}