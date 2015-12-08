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
package com.pileproject.drive.programming.visual.layout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumTextHolder;
import com.pileproject.drive.view.FrameView;

import java.util.ArrayList;

/**
 * Manager of PlacingBlockSpaceLayout
 * This manages it to show the progression of execution.
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public class ProgressSpaceManager extends BlockSpaceManager {
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private FrameView mFrame = null;

    public ProgressSpaceManager(Context context, BlockSpaceLayout layout) {
        super(context, layout);
    }

    public void addBlocks(ArrayList<BlockBase> blocks) {
        for (BlockBase block : blocks) {
            if (block instanceof NumTextHolder) {
                // set OnTouchListener to TextView
                TextView numText = ((NumTextHolder) block).getTextView();
                numText.setFocusable(false);
                numText.setFocusableInTouchMode(false);
                numText.setEnabled(false);
            }
            mLayout.addView(block, new LayoutParams(WC, WC));
        }
    }

    /**
     * Emphasize the selected block
     *
     * @param index
     */
    public void emphasizeBlock(int index) {
        // Get the target block
        // Add the number of default children count of PlacingSpaceLayout
        View view =
                mLayout.getChildAt(index + mLayout.getDefaultChildrenCount());
        if (view instanceof BlockBase) {
            if (mFrame != null) {
                mLayout.removeView(mFrame);
            }
            // Create frame for emphasizing
            mFrame = new FrameView(mContext,
                                   view.getLeft(),
                                   view.getTop(),
                                   view.getRight(),
                                   view.getBottom());
            mLayout.addView(mFrame, new BlockSpaceLayout.LayoutParams(WC, WC));
        }
    }
}
