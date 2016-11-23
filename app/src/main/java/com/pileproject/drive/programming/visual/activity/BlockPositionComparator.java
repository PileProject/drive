/**
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
package com.pileproject.drive.programming.visual.activity;

import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.Comparator;


/**
 * For sorting blocks
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class BlockPositionComparator implements Comparator<BlockBase> {
    public static final int ASC = 1; // ascending order
    public static final int DESC = -1; // descending order
    private final int mSort; // sort order

    /**
     * Constructor
     *
     * Default sort order is ascending
     */
    public BlockPositionComparator() {
        mSort = ASC;
    }

    /**
     * Constructor (Select sort order)
     *
     * @param sort Sort order
     *             ascending order：ViewComparator.ASC
     *             descending order：ViewComparator.DESC
     */
    public BlockPositionComparator(int sort) {
        mSort = sort;
    }

    @Override
    public int compare(BlockBase a, BlockBase b) {
        if (a == null && b == null) {
            return 0; // a = b
        } else if (a == null) {
            return mSort; // a > b
        } else if (b == null) {
            return -1 * mSort; // a < b
        }

        int result = (a.getTop() * 10000 + a.getLeft()) - (b.getTop() * 10000 + b.getLeft());

        return result * mSort;
    }
}
