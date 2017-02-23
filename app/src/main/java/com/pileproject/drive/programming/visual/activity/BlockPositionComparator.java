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
package com.pileproject.drive.programming.visual.activity;

import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.Comparator;


/**
 * A comparator that sorts {@link BlockBase}s according to the position of them. The position (x, y) is reduced to a
 * single value by
 */
public class BlockPositionComparator implements Comparator<BlockBase> {
    public static final int ASC = 1; // ascending order
    public static final int DESC = -1; // descending order
    private final int mSort; // sort order
    private static final int OFFSET = 10000;

    /**
     * The default sort order is ascending
     */
    public BlockPositionComparator() {
        mSort = ASC;
    }

    /**
     * A constructor with an argument to specify the sort order.
     *
     * @param sort the sort order which is one of the following options
     *             ascending: {@link BlockPositionComparator#ASC}
     *             descending: {@link BlockPositionComparator#DESC}
     */
    public BlockPositionComparator(int sort) {
        mSort = sort;
    }

    private int reducePosition(BlockBase a) {
        // NOTE: the priority of the y position is higher than that of the x position
        return a.getTop() * OFFSET + a.getLeft();
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

        int result = reducePosition(a) - reducePosition(b);
        return result * mSort;
    }
}
