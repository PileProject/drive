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

package com.pileproject.drive.programming.visual.event;


import com.pileproject.drive.programming.visual.layout.BlockSpaceLayout;

/**
 * Base class of EventBase for undo and redo
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 4-June-2013
 */
public abstract class EventBase {
    protected final int mElementCount;
    protected final int mIndex;

    protected EventBase(int elementCount, int index) {
        mElementCount = elementCount;
        mIndex = index;
    }

    public int getElementCount() {
        return mElementCount;
    }

    public int getIndex() {
        return mIndex;
    }

    /**
     * Undo method
     *
     * @param layout       Layout that has blocks
     * @param elementCount Number that shows how many UNDOs should be done at
     *                     once
     * @return
     */
    public abstract EventBase undo(BlockSpaceLayout layout, int elementCount);
}
