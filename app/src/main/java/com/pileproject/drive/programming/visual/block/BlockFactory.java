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

package com.pileproject.drive.programming.visual.block;

import android.content.Context;
import android.support.annotation.IntDef;

import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionEndBlock;
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Factory that creates blocks
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 18-June-2013
 */
public class BlockFactory {
    public static final int SEQUENCE = 0;
    public static final int REPETITION = 1;
    public static final int SELECTION = 2;
    public static final int LOAD = 4;

    private BlockFactory() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockBase> Class<T> getClassForName(String className) throws RuntimeException {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid class name '" + className + "'", e);
        }
    }

    private static BlockBase create(Class<? extends BlockBase> blockClass) throws RuntimeException {
        try {
            Constructor<? extends BlockBase> constructor = blockClass.getConstructor(Context.class);
            return constructor.newInstance(DriveApplication.getContext());
        } catch (Exception e) {
            // there's no way to recover. let App die.
            throw new RuntimeException("Failed to instantiate " + blockClass, e);
        }
    }

    private static List<BlockBase> createSequenceBlock(Class<? extends BlockBase> blockClass) {
        return Collections.singletonList(create(blockClass));
    }

    private static List<BlockBase> createRepetitionBlock(Class<? extends BlockBase> blockClass) {
        return Arrays.asList(new RepetitionEndBlock(DriveApplication.getContext()), create(blockClass));
    }

    private static List<BlockBase> createSelectionBlock(Class<? extends BlockBase> blockClass) {
        return Arrays.asList(new SelectionEndBlock(DriveApplication.getContext()), create(blockClass));
    }

    /**
     * Returns one or two instances of block whose class is <code>blockName</code>.
     * Type of block can be specified with @{howToMake}, which also determines the number
     * of blocks this function returns.
     *
     * @param howToMake type of block. must be one of {@link BlockFactory#SEQUENCE},
     *                  {@link BlockFactory#REPETITION}, {@link BlockFactory#SELECTION},
     *                  and {@link BlockFactory#LOAD}
     * @param blockName class name of block to be created
     * @return list of blocks. the first element of the list is end block
     *                  if block type is {@link BlockFactory#SELECTION} or
     *                  {@link BlockFactory#REPETITION}.
     *
     * @exception RuntimeException if <code>blockName</code> is not supported block class name
     *                             or <code>howToMake</code> is none of the values listed above
     */
    public static List<BlockBase> createBlocks(@HowToMake int howToMake, String blockName) {
        Class<? extends BlockBase> blockClass = getClassForName(blockName);

        switch (howToMake) {
            case SEQUENCE:
            case LOAD: {
                return createSequenceBlock(blockClass);
            }

            case REPETITION: {
                return createRepetitionBlock(blockClass);
            }

            case SELECTION: {
                return createSelectionBlock(blockClass);
            }
        }

        throw new RuntimeException("Do not know how to make a block of " + blockName);
    }

    @IntDef({SEQUENCE, REPETITION, SELECTION, LOAD})
    public @interface HowToMake {
    }
}
