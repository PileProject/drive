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
import com.pileproject.drive.programming.visual.block.repetition.RepetitionHasNumText;

import java.util.ArrayList;


public class RepetitionSyntaxTree extends SyntaxTreeBase {
    public ArrayList<SyntaxTreeBase> body;
    private BlockBase mBeginblock;
    private BlockBase mEndblock;
    private int mNumRepetition;

    public RepetitionSyntaxTree(BlockBase begin, BlockBase end, ArrayList<SyntaxTreeBase> statement) {
        mBeginblock = begin;
        mEndblock = end;
        body = statement;

        if (mBeginblock instanceof RepetitionHasNumText) {
            mNumRepetition = ((RepetitionHasNumText) mBeginblock).getNum();
        }
    }

    public int getRepetitionNum() {
        return mNumRepetition;
    }

    @Override
    protected int align(Resources res, int top, int left, int margin) {
        int repblock_heightpx = res.getDimensionPixelSize(R.dimen.block_repetition_height);

        // TODO(tatsuya): are these necessary?
        mBeginblock.setLeft(left);
        mBeginblock.setTop(top);

        mBeginblock.layout(mBeginblock.getLeft(),
                           mBeginblock.getTop(),
                           mBeginblock.getLeft() + mBeginblock.getWidth(),
                           mBeginblock.getTop() + mBeginblock.getHeight());

        top += repblock_heightpx + margin;

        for (SyntaxTreeBase node : body) {
            top = node.align(res, top, left, margin);
        }

        // TODO(tatsuya): are these necessary?
        mEndblock.setLeft(left);
        mEndblock.setTop(top);

        mEndblock.layout(mEndblock.getLeft(),
                         mEndblock.getTop(),
                         mEndblock.getLeft() + mEndblock.getWidth(),
                         mEndblock.getTop() + mEndblock.getHeight());

        top += repblock_heightpx + margin;
        return top;
    }
}
