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
package com.pileproject.drive.programming.visual.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.CycleInterpolator;
import android.widget.LinearLayout.LayoutParams;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumberTextHolder;
import com.pileproject.drive.view.NumberSelectSeekBarView;
import com.pileproject.drive.view.NumberSelectViewBase;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A manager of BlockSpaceLayout that helps users to make programs.
 */
public class ProgrammingSpaceManager extends BlockSpaceManagerBase {

    private final BlockOnTouchListener mOnTouchListener = new BlockOnTouchListener();

    public ProgrammingSpaceManager(Context context, BlockSpaceLayout layout) {
        super(context, layout);
    }

    private void setListeners(BlockBase block) {
        // set OnTouchListener to the block
        block.setOnTouchListener(mOnTouchListener);

        if (block instanceof NumberTextHolder) {
            ((NumberTextHolder) block).setOnLongClickTextViewListener(
                    new OnTouchNumberTextListener((NumberTextHolder) block));
        }
    }

    @Override
    public void addBlocks(List<BlockBase> blocks) {
        // emphasize block animation
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        alpha.setDuration(1000);
        alpha.setInterpolator(new CycleInterpolator(3));

        for (BlockBase block : blocks) {
            setListeners(block);
            mLayout.addView(block, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            block.setAnimation(alpha);
        }
    }

    private class BlockOnTouchListener implements OnTouchListener {
        int currentX; // the left position of this view (x coordinate)
        int currentY; // the top position of this view (y coordinate)
        int offsetX; // the x position of user's finger
        int offsetY; // the y position of user's finger
        int originX; // the original x position
        int originY; // the original y position

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Get the position of user's finger
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // NOTE: this method changes the index of views
                    // therefore undo and redo cannot be done properly.
                    view.bringToFront();

                    // update variables
                    originX = currentX = view.getLeft();
                    originY = currentY = view.getTop();
                    offsetX = x;
                    offsetY = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    int diffX = offsetX - x;
                    int diffY = offsetY - y;
                    currentX -= diffX;
                    currentY -= diffY;

                    moveViewWithinItsParent(view, currentX, currentY);

                    offsetX = x;
                    offsetY = y;
                    break;

                case MotionEvent.ACTION_UP:
                    // this view will be removed if it is on the trash box
                    if (mLayout.isOnTrash(view)) {
                        mLayout.removeView(view);
                    }
                    break;
            }
            return true;
        }

        private void moveViewWithinItsParent(View view, int currentX, int currentY) {
            ViewGroup parent = (ViewGroup) view.getParent();
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();

            if (currentX < 0) {
                currentX = 0;
            }
            if (currentX > parent.getRight() - view.getWidth()) {
                currentX = parentWidth - view.getWidth();
            }
            if (currentY < 0) {
                currentY = 0;
            }
            if (currentY > parentHeight - view.getHeight()) {
                currentY = parentHeight - view.getHeight();
            }

            view.layout(currentX, currentY, currentX + view.getWidth(), currentY + view.getHeight());
        }
    }

    private class OnTouchNumberTextListener implements View.OnLongClickListener {

        private final NumberTextHolder mHolder;

        public OnTouchNumberTextListener(NumberTextHolder parent) {
            mHolder = parent;
        }

        @Override
        public boolean onLongClick(View v) {

            final NumberSelectViewBase numberSelectView = new NumberSelectSeekBarView(mContext,
                    mHolder.getValue(), mHolder.getRange(), mHolder.getPrecision(), mHolder.getUnit());

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.programming_pleaseSelectNumbers)
                    .setView(numberSelectView)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHolder.setValue(numberSelectView.getValue());
                        }
                    })
                    .show();

            return true;
        }
    }
}
