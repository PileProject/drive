/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.programming.visual.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
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
import com.pileproject.drive.programming.visual.activity.ProgrammingActivityBase;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.NumTextHolder;
import com.pileproject.drive.programming.visual.event.AddEvent;
import com.pileproject.drive.programming.visual.event.ChangeNumberEvent;
import com.pileproject.drive.programming.visual.event.DeleteEvent;
import com.pileproject.drive.programming.visual.event.EventBase;
import com.pileproject.drive.programming.visual.event.MoveEvent;
import com.pileproject.drive.util.Range;
import com.pileproject.drive.util.Unit;
import com.pileproject.drive.view.NumberSelectSeekBarView;
import com.pileproject.drive.view.NumberSelectView;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Manager of BlockSpaceLayout This helps users make programs.
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public class ProgrammingSpaceManager extends BlockSpaceManager {
	private Stack<EventBase> mLogForUndo = null;
	private Stack<EventBase> mLogForRedo = null;

	private ProgrammingActivityBase.UndoAndRedoButtonsManager mButtonsManager;

	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private final String TAG = "ProgrammingSpaceManger";

	public ProgrammingSpaceManager(Context context,
			BlockSpaceLayout layout,
			ProgrammingActivityBase.UndoAndRedoButtonsManager buttonsManager) {
		super(context, layout);
		mButtonsManager = buttonsManager;

		mLogForUndo = new Stack<EventBase>();
		mLogForRedo = new Stack<EventBase>();
	}

	public boolean canUndo() {
		return !mLogForUndo.empty();
	}

	public boolean undo() {
		if (mLogForUndo.empty())
			return false;

		int elementCount = mLogForUndo.peek().getElementCount();

		// Undo for elementCount times
		for (int i = 0; i < elementCount; i++) {
			EventBase diff = mLogForUndo.pop();

			Log.d(TAG, diff.getClass().getSimpleName());

			EventBase forRedo = diff.undo(mLayout, elementCount);
			if (diff instanceof DeleteEvent) {
				BlockBase block = (BlockBase) mLayout.getChildAt(forRedo
						.getIndex());
				setListeners(block);
			}
			mLogForRedo.push(forRedo);
		}
		return true;
	}

	public boolean canRedo() {
		return !mLogForRedo.empty();
	}

	public boolean redo() {
		if (mLogForRedo.empty())
			return false;

		int elementCount = mLogForRedo.peek().getElementCount();

		// Undo for elementCount times
		for (int i = 0; i < elementCount; i++) {
			EventBase diff = mLogForRedo.pop();
			EventBase forUndo = diff.undo(mLayout, elementCount);
			if (diff instanceof DeleteEvent) {
				BlockBase block = (BlockBase) mLayout.getChildAt(forUndo
						.getIndex());
				setListeners(block);
			}
			mLogForUndo.push(forUndo);
		}
		return true;
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

			// Create new AddEventBase
			EventBase diff = new AddEvent(blocks.size(), mLayout.indexOfChild(block));
			mLogForUndo.push(diff);
		}
		mLogForRedo.clear();
	}

	@Override
	public void deleteAllBlocks() {
		super.deleteAllBlocks();
		mLogForUndo.clear();
		mLogForRedo.clear();
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
			final int numOfDecimalDigits  = digit[1];
			
			final Range<Double> range = Range.closed(mParent.getMin(), mParent.getMax());
			final Unit unit = mParent.getUnit();

			final NumberSelectView numberSelectView = new NumberSelectSeekBarView(mContext, range, unit, numOfIntegralDigits, numOfDecimalDigits);

			numberSelectView.setNum(oldNum);

			// Create a new AlertDialog to pick the number
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

			dialog.setTitle(R.string.programming_pleaseSelectNumbers);
			dialog.setView(numberSelectView);
			dialog.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					double rawNum = numberSelectView.getSelectedNum();

					int newNum = (int) (rawNum * Math.pow(10,
							numOfDecimalDigits));

					// Set new value
					mParent.setNum(newNum);

					// Create ChangeNumberEvent
					EventBase diff
					= new ChangeNumberEvent(1, mLayout.indexOfChild((View) mParent), oldNum);

					mLogForUndo.push(diff);
					mLogForRedo.clear();

					// Check Undo/Redo buttons' workability
					mButtonsManager.checkButtonsWorkability();
				}
			});
			dialog.show();
			return true;
		}
	}

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
				int elementCount = 1;

				EventBase diff = new MoveEvent(elementCount++,
						mLayout.indexOfChild(view), originX, originY);
				mLogForUndo.push(diff);

				// This view will be removed if it is on the trash box
				if (mLayout.isOnTrash(view)) {
					if (view instanceof NumTextHolder) {
						// Get old value
						int oldNum = ((NumTextHolder) view).getNum();

						diff = new ChangeNumberEvent(elementCount++,
								mLayout.indexOfChild(view), oldNum);
						mLogForUndo.push(diff);
					}
					diff = new DeleteEvent(elementCount++,
							mLayout.indexOfChild(view), view.getClass()
									.getName());
					mLogForUndo.push(diff);
					mLayout.removeView(view);
				}
				mLogForRedo.clear();

				// Check Undo/Redo buttons' workability
				mButtonsManager.checkButtonsWorkability();
				break;
			}
			return true;
		}
		
		private void moveViewWithinItsParent(View view, int currentX, int currentY) {
			ViewGroup parent = (ViewGroup)view.getParent();
			int parentWidth = parent.getWidth();
			int parentHeight = parent.getHeight();
			
			if (currentX < 0)
				currentX = 0;
			if (currentX > parent.getRight() - view.getWidth())
				currentX = parentWidth - view.getWidth();
			if (currentY < 0)
				currentY = 0;
			if (currentY > parentHeight - view.getHeight())
				currentY = parentHeight - view.getHeight();
			
			view.layout(currentX, currentY,
					currentX + view.getWidth(), currentY + view.getHeight());
		}
	};
}
