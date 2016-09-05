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
import com.pileproject.drive.programming.visual.block.repetition.RepetitionBreakBlock;


public class SequenceSyntaxTree extends SyntaxTreeBase {
    private BlockBase mDefblock;

    public BlockBase getDefinition() {
        return mDefblock;
    }

    public SequenceSyntaxTree(BlockBase define) {
        mDefblock = define;
    }

    @Override
    protected int align(Resources res, int top, int left, int margin) {
        int height;

        if (mDefblock.getKind() == RepetitionBreakBlock.class) {
            height = res.getDimensionPixelSize(R.dimen.block_repetition_break_height);
        } else {
            height = res.getDimensionPixelSize(R.dimen.block_sequence_height);
        }

        // TODO(tatsuya): are these necessary?
        mDefblock.setLeft(left);
        mDefblock.setTop(top);

        mDefblock.layout(mDefblock.getLeft(),
                         mDefblock.getTop(),
                         mDefblock.getLeft() + mDefblock.getWidth(),
                         mDefblock.getTop() + mDefblock.getHeight());

        return top + height + margin;
    }
}
