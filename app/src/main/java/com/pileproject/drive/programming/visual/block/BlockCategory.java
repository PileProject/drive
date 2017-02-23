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
package com.pileproject.drive.programming.visual.block;

import android.support.annotation.IntDef;

import static com.pileproject.drive.programming.visual.block.BlockCategory.REPETITION;
import static com.pileproject.drive.programming.visual.block.BlockCategory.SELECTION;
import static com.pileproject.drive.programming.visual.block.BlockCategory.SEQUENCE;

/**
 * An annotation that represents a category of the blocks in this app.
 * <ul>
 *     <li><code>SEQUENCE</code>: Sequence block</li>
 *     <li><code>REPETITION</code>: Repetition block</li>
 *     <li><code>SELECTION</code>: Selection block</li>
 * </ul>
 * <p>
 * Do not confuse with {@link BlockBase.BlockKind}: this annotation is for wider concept
 * than that of {@link BlockBase.BlockKind} represents.
 */
@IntDef({SEQUENCE, REPETITION, SELECTION})
public @interface BlockCategory {
    int SEQUENCE = 0;
    int REPETITION = 1;
    int SELECTION = 2;
}
