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
import android.graphics.Color;
import android.util.AttributeSet;

import com.pileproject.drive.R;
import com.pileproject.drive.execution.NxtController;

public class NxtMotorPortTextView extends PortTextView {

    public NxtMotorPortTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String getMotorName(Context context, int attachmentType) {
        switch (attachmentType) {
            case NxtController.MotorProperty.MOTOR_LEFT:
                return context.getString(R.string.motors_left);
            case NxtController.MotorProperty.MOTOR_RIGHT:
                return context.getString(R.string.motors_right);
        }
        return "";
    }

    public static int getMotorColor(int motorType) {
        switch (motorType) {
            case NxtController.MotorProperty.MOTOR_LEFT:
                return Color.rgb(70, 89, 183);
            case NxtController.MotorProperty.MOTOR_RIGHT:
                return Color.rgb(214, 133, 52);
        }
        return Color.GRAY;
    }

    @Override
    public int getAttachmentType() {
        return mAttachmentType;
    }

    @Override
    public void setAttachmentType(int attachmentType) {
        mAttachmentType = attachmentType;
        this.setText(getMotorName(mContext, attachmentType));
        this.setBackgroundColor(getMotorColor(attachmentType));
    }
}
