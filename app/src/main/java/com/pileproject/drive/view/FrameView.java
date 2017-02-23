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
package com.pileproject.drive.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * A frame view to emphasize blocks during program executions.
 */
public class FrameView extends View {
    private Paint mPaint;
    private Rect mRect;

    /**
     * @param context the context of the {@link Activity} which shows this view
     * @param l the left of a block
     * @param t the top of a block
     * @param r the right of a block
     * @param b the bottom of a block
     */
    public FrameView(Context context, int l, int t, int r, int b) {
        super(context);

        // make a paint object and a rectangle
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);

        mRect = new Rect(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw a rectangle around a block
        canvas.drawRect(mRect, mPaint);
    }
}
