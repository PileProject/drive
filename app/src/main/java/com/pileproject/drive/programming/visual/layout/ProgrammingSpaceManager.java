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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.CycleInterpolator;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumTextHolder;
import com.pileproject.drive.util.Range;
import com.pileproject.drive.util.Unit;
import com.pileproject.drive.view.NumberSelectSeekBarView;
import com.pileproject.drive.view.NumberSelectView;

import java.util.ArrayList;

/**
 * Manager of BlockSpaceLayout This helps users make programs.
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public class ProgrammingSpaceManager extends BlockSpaceManager {
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final String TAG = "ProgrammingSpaceManger";
    public final OnTouchListener mMoveBlock = new OnTouchListener() {
        int currentX; // The left position of this view (x coordinate)
        int currentY; // The top position of this view (y coordinate)
        int offsetX; // The x position of user's finger
        int offsetY; // The y position of user's finger
        int originX; // The original x position
        int originY; // The original y position

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Get the position of user's finger
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getActionMasked()) {
                // Touched
                case MotionEvent.ACTION_DOWN:
                    // Bring the view touched to the front
                    // NOT USED this method changes the index of views
                    // therefore undo and redo cannot be done properly.
                    // view.bringToFront();

                    // Update variables
                    originX = currentX = view.getLeft();
                    originY = currentY = view.getTop();
                    offsetX = x;
                    offsetY = y;
                    break;

                // Moving
                case MotionEvent.ACTION_MOVE:
                    int diffX = offsetX - x;
                    int diffY = offsetY - y;
                    currentX -= diffX;
                    currentY -= diffY;

                    moveViewWithinItsParent(view, currentX, currentY);

                    offsetX = x;
                    offsetY = y;
                    break;

                // Finished Moving
                case MotionEvent.ACTION_UP:
                    // This view will be removed if it is on the trash box
                    if (mLayout.isOnTrash(view)) {
                        mLayout.removeView(view);
                    }
                    break;
            }
            return true;
        }

        private void moveViewWithinItsParent(
                View view, int currentX, int currentY) {
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
    };

    public ProgrammingSpaceManager(Context context, BlockSpaceLayout layout) {
        super(context, layout);
    }

    private void setListeners(BlockBase block) {
        // set OnTouchListener to the block
        block.setOnTouchListener(mMoveBlock);
        // set OnTouchListener to TextView
        if (block instanceof NumTextHolder) {
            TextView numText = ((NumTextHolder) block).getTextView();
            numText.setOnLongClickListener(new OnTouchNumTextListener(block));
        }
    }

    public void addBlocks(ArrayList<BlockBase> blocks) {
        // Emphasize block animation
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        alpha.setDuration(1000);
        alpha.setInterpolator(new CycleInterpolator(3));

        for (BlockBase block : blocks) {
            setListeners(block);

            mLayout.addView(block, new LayoutParams(WC, WC));

            block.setAnimation(alpha);
        }
    }

    @Override
    public void deleteAllBlocks() {
        super.deleteAllBlocks();
    }

    class OnTouchNumTextListener implements OnLongClickListener {
        NumTextHolder mParent;

        public OnTouchNumTextListener(BlockBase parent) {
            mParent = (NumTextHolder) parent;
        }

        @Override
        public boolean onLongClick(View v) {
            // Get old value
            final int oldNum = mParent.getNum();

            // Create a new NumberPicker View and set the old number
            Integer[] digit = mParent.getDigit();

            final int numOfIntegralDigits = digit[0];
            final int numOfDecimalDigits = digit[1];

            final Range<Double> range = Range.closed(mParent.getMin(), mParent.getMax());
            final Unit unit = mParent.getUnit();

            final NumberSelectView numberSelectView =
                    new NumberSelectSeekBarView(mContext, range, unit, numOfIntegralDigits, numOfDecimalDigits);

            numberSelectView.setNum(oldNum);

            // Create a new AlertDialog to pick the number
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

            dialog.setTitle(R.string.programming_pleaseSelectNumbers);
            dialog.setView(numberSelectView);
            dialog.setPositiveButton(R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    double rawNum = numberSelectView.getSelectedNum();

                    int newNum = (int) (rawNum * Math.pow(10, numOfDecimalDigits));

                    // Set new value
                    mParent.setNum(newNum);
                }
            });
            dialog.show();
            return true;
        }
    }
}
