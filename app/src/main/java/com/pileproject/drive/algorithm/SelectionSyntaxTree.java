/*
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

package com.pileproject.drive.algorithm;

import android.content.res.Resources;

import com.pileproject.drive.R;
import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.ArrayList;

public class SelectionSyntaxTree extends SyntaxTreeBase {
    public ArrayList<SyntaxTreeBase> truebody;
    public ArrayList<SyntaxTreeBase> falsebody;
    private BlockBase mBeginblock;
    private BlockBase mEndblock;

    public SelectionSyntaxTree(
            BlockBase begin,
            BlockBase end,
            ArrayList<SyntaxTreeBase> truestatement,
            ArrayList<SyntaxTreeBase> falsestatement) {
        mBeginblock = begin;
        mEndblock = end;
        truebody = truestatement;
        falsebody = falsestatement;
    }

    @Override
    protected int align(Resources res, int top, int left, int margin) {
        int beginblock_heightpx = res.getDimensionPixelSize(R.dimen.block_selection_height);
        int beginblock_widthpx = res.getDimensionPixelSize(R.dimen.block_selection_width);

        // TODO(tatsuya): are these necessary?
        mBeginblock.setLeft(left);
        mBeginblock.setTop(top);

        mBeginblock.layout(mBeginblock.getLeft(),
                           mBeginblock.getTop(),
                           mBeginblock.getLeft() + mBeginblock.getWidth(),
                           mBeginblock.getTop() + mBeginblock.getHeight());

        int toppx_true = top + margin;
        int leftpx_true = left + beginblock_widthpx + margin;
        for (SyntaxTreeBase node : truebody) {
            toppx_true = node.align(res, toppx_true, leftpx_true, margin);
        }

        int toppx_false = top + beginblock_heightpx + margin;
        for (SyntaxTreeBase node : falsebody) {
            toppx_false = node.align(res, toppx_false, left, margin);
        }

        top = Math.max(toppx_true, toppx_false);
        int endblock_heightpx = res.getDimensionPixelSize(R.dimen.block_selection_end_height);

        // TODO(tatsuya): are these necessary?
        mEndblock.setLeft(left);
        mEndblock.setTop(top);

        mEndblock.layout(mEndblock.getLeft(),
                         mEndblock.getTop(),
                         mEndblock.getLeft() + mEndblock.getWidth(),
                         mEndblock.getTop() + mEndblock.getHeight());

        top += endblock_heightpx + margin;
        return top;
    }

}
