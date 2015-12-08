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

package com.pileproject.drive.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.TextView;

import com.pileproject.drive.R;
import com.pileproject.drive.util.SharedPreferencesWrapper;

public abstract class PortTextView extends TextView {
	final private boolean mIsAcceptable;
	final private String mPortName;
	final private String mPortType;

	final protected Context mContext;
	protected int mAttachmentType;

	private MediaPlayer mSoundEffectOfMovingBlock;
	
	public PortTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		mSoundEffectOfMovingBlock = MediaPlayer.create(getContext(), R.raw.pon);

		TypedArray tar = context.obtainStyledAttributes(attrs, R.styleable.PortTextView);
		String port;
		mPortName = tar.getString(R.styleable.PortTextView_portName);
		port = tar.getString(R.styleable.PortTextView_portType);
		mPortType = (port != null) ? port : "";
		mIsAcceptable = (mPortName != null);
		tar.recycle();

		setOnTouchListener((v, arg1) -> {
			v.startDrag(null, new DragShadowBuilder(v), v, 0);
			return true;
		});
	}
	
	@Override
	public boolean onDragEvent(DragEvent event) {
		final int action = event.getAction();
		final PortTextView localState = (PortTextView) event.getLocalState();

		if (action == DragEvent.ACTION_DRAG_STARTED) {
			return true;
		}
		else if (action == DragEvent.ACTION_DROP) {
			if (localState.mPortType.equals(this.mPortType)) {
				swap(localState, this);

				mSoundEffectOfMovingBlock.start(); // play sound

				if (mIsAcceptable) {
					SharedPreferencesWrapper.saveIntPreference(mContext, mPortName, mAttachmentType);
				}

				if (localState.mIsAcceptable) {
					SharedPreferencesWrapper.saveIntPreference(mContext, localState.mPortName, localState.mAttachmentType);
				}
			}
			return true;
		}
		return false;
	}

	public abstract void setAttachmentType(int attachmentType);

	public abstract int getAttachmentType();

	public String getPortName() {
		return mPortName;
	}

	public static void swap(PortTextView lhs, PortTextView rhs) {
		int lhsAttachmentType = lhs.getAttachmentType();
		lhs.setAttachmentType(rhs.getAttachmentType());
		rhs.setAttachmentType(lhsAttachmentType);
	}
}
