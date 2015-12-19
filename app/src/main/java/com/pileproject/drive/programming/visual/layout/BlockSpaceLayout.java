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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pileproject.drive.R;

/**
 * Layout for making programs
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public class BlockSpaceLayout extends FrameLayout {
    private ImageView mTrashBox;
    private int mDefaultChildrenNum;
    private static final int MARGINE = 20;

    public BlockSpaceLayout(Context context, AttributeSet attr) {
        super(context, attr);
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_block_space, this);
        mTrashBox = (ImageView) layout.findViewById(R.id.placing_block_space_trash);
        mDefaultChildrenNum = getChildCount();
    }

    /**
     * Check a block is on the trash box or not
     *
     * @param view a block
     * @return boolean
     * true - a block is on the trash box, false - otherwise
     */
    public boolean isOnTrash(View view) {
        return view.getLeft() > mTrashBox.getLeft() - view.getWidth() + MARGINE &&
                view.getLeft() < mTrashBox.getRight() &&
                view.getTop() > mTrashBox.getTop() - view.getHeight() + MARGINE &&
                view.getTop() < mTrashBox.getBottom();
    }

    /**
     * Remove all views attached to this layout, then add a trash box imageView
     */
    @Override
    public void removeAllViews() {
        super.removeAllViews();
        // Reload layout because this layout needs a trash box
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.layout_block_space, this);
        mTrashBox = (ImageView) layout.findViewById(R.id.placing_block_space_trash);
    }

    /**
     * Return the number of default children
     *
     * @return
     */
    public int getDefaultChildrenCount() {
        return mDefaultChildrenNum;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) { // Haven't been gone

                // a new view
                if (child.getLeft() == child.getRight() && child.getTop() == child.getBottom()) {
                    child.layout(child.getLeft(),
                                 child.getTop(),
                                 child.getLeft() + child.getMeasuredWidth(),
                                 child.getTop() + child.getMeasuredHeight());
                } else {
                    child.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                }
            }
        }
    }
}
