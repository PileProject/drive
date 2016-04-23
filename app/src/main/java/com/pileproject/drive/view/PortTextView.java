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

package com.pileproject.drive.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.R;

public abstract class PortTextView extends TextView {
    final private boolean mIsFreePort;
    final private String mPortName;
    final private String mPortType;
    protected String mDeviceType;


    public PortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tar = context.obtainStyledAttributes(attrs, R.styleable.PortTextView);
        mPortName = tar.getString(R.styleable.PortTextView_portName);
        mPortType = tar.getString(R.styleable.PortTextView_portType);
        mIsFreePort = (mPortName == null); // a free port has no name
        tar.recycle();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startDrag(null, new DragShadowBuilder(v), v, 0);
                return true;
            }
        });
    }

    public static void swap(PortTextView lhs, PortTextView rhs) {
        String lhsAttachmentType = lhs.getDeviceType();
        lhs.setDeviceType(rhs.getDeviceType());
        rhs.setDeviceType(lhsAttachmentType);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        final PortTextView localState = (PortTextView) event.getLocalState();

        if (action == DragEvent.ACTION_DRAG_STARTED) {
            return true;
        }
        else if (action == DragEvent.ACTION_DROP) {
            // match the port type (e.g. "motor" and "motor", "sensor" and "sensor")
            if (localState.mPortType.equals(this.mPortType)) {
                swap(localState, this);

                MediaPlayer soundEffectOfMovingBlock = MediaPlayer.create(getContext(), R.raw.pon);
                soundEffectOfMovingBlock.start(); // play sound
                soundEffectOfMovingBlock.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });

                if (mIsFreePort) removePortConnection(mPortName);
                else savePortConnection(mPortName, mDeviceType);

                if (localState.mIsFreePort) removePortConnection(localState.mPortName);
                else savePortConnection(localState.mPortName, localState.mDeviceType);
            }
            return true;
        }
        return false;
    }

    protected abstract void savePortConnection(String port, String device);

    protected abstract void removePortConnection(String port);

    public abstract String getDeviceType();

    public abstract void setDeviceType(String deviceType);

    public String getPortName() {
        return mPortName;
    }
}
