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

/**
 * a TextView which expresses a port
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 5-June-2013
 */
public abstract class PortTextViewBase extends TextView {
    final private boolean mIsFreePort;
    final private String mPortName;
    final private String mPortType;
    protected String mDeviceType;

    public PortTextViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tar = context.obtainStyledAttributes(attrs, R.styleable.PortTextViewBase);
        mPortName = tar.getString(R.styleable.PortTextViewBase_portName);
        mPortType = tar.getString(R.styleable.PortTextViewBase_portType);
        mIsFreePort = (mPortName == null); // NOTE: a free port has no name
        tar.recycle();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startDrag(null, new DragShadowBuilder(v), v, 0);
                return true;
            }
        });
    }

    /**
     * swap ports
     * @param lhs a port
     * @param rhs another port
     */
    public static void swap(PortTextViewBase lhs, PortTextViewBase rhs) {
        String lhsAttachmentType = lhs.getDeviceType();
        lhs.setDeviceType(rhs.getDeviceType());
        rhs.setDeviceType(lhsAttachmentType);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        final PortTextViewBase localState = (PortTextViewBase) event.getLocalState();

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

                // swap port the connection status
                if (mIsFreePort) removePortConnection(mPortName);
                else savePortConnection(mPortName, mDeviceType);

                if (localState.mIsFreePort) removePortConnection(localState.mPortName);
                else savePortConnection(localState.mPortName, localState.mDeviceType);
            }
            return true;
        }
        return false;
    }

    /**
     * save a connection setting
     *
     * @param port
     * @param device
     */
    protected abstract void savePortConnection(String port, String device);

    /**
     * remove a port connection setting
     *
     * @param port
     */
    protected abstract void removePortConnection(String port);

    /**
     * a getter for the type of device
     *
     * @return the type of device in string
     */
    public abstract String getDeviceType();

    /**
     * a setter for the type of device
     *
     * @param deviceType
     */
    public abstract void setDeviceType(String deviceType);

    /**
     * a getter for the name of port
     *
     * @return the name of port in string
     */
    public String getPortName() {
        return mPortName;
    }
}
