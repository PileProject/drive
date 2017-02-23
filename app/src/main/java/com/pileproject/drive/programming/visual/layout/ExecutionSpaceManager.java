/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
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
package com.pileproject.drive.programming.visual.layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumberTextHolder;
import com.pileproject.drive.view.FrameView;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * A manager of {@link BlockSpaceLayout} that manages the layout to show the progress of execution.
 */
public class ExecutionSpaceManager extends BlockSpaceManagerBase {
    private FrameView mFrame;

    public ExecutionSpaceManager(Context context, BlockSpaceLayout layout) {
        super(context, layout);
    }

    @Override
    public void addBlocks(List<BlockBase> blocks) {

        for (BlockBase block : blocks) {

            if (block instanceof NumberTextHolder) {
                ((NumberTextHolder) block).enableTextView(false);
            }

            mLayout.addView(block, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        }
    }

    /**
     * Emphasizes the current block with a {@link FrameView}.
     *
     * @param index the index of the current block
     */
    public void emphasizeBlock(int index) {
        // get the target block
        // add the number of default children count of BlockSpaceLayout
        View view = mLayout.getChildAt(index + mLayout.getDefaultChildrenCount());

        if (view instanceof BlockBase) {
            if (mFrame != null) mLayout.removeView(mFrame);

            // create frame to emphasize the current block
            mFrame = new FrameView(mContext, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            mLayout.addView(mFrame, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        }
    }
}
