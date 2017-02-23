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

import android.content.Context;
import android.support.annotation.NonNull;

import com.pileproject.drive.app.DriveApplication;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionBreakBlock;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionEndBlock;
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pileproject.drive.programming.visual.block.BlockCategory.REPETITION;
import static com.pileproject.drive.programming.visual.block.BlockCategory.SELECTION;
import static com.pileproject.drive.programming.visual.block.BlockCategory.SEQUENCE;

/**
 * A factory class that creates {@link BlockBase}s.
 */
public class BlockFactory {

    private BlockFactory() {
        throw new AssertionError("This class cannot be instantiated");
    }

    private static <T extends BlockBase> Class<T> getClassForName(String className) throws RuntimeException {
        try {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) Class.forName(className);
            return clazz;
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
        if (blockClass == RepetitionBreakBlock.class) {
            return Collections.singletonList(create(blockClass));
        } else {
            return Arrays.asList(new RepetitionEndBlock(DriveApplication.getContext()), create(blockClass));
        }
    }

    private static List<BlockBase> createSelectionBlock(Class<? extends BlockBase> blockClass) {
        return Arrays.asList(new SelectionEndBlock(DriveApplication.getContext()), create(blockClass));
    }

    /**
     * Returns one or two instances of block whose class is <code>blockName</code>.
     * Type of block can be specified with @{howToMake}, which also determines the number
     * of blocks this function returns.
     *
     * @param blockCategory the type of block which must be one of {@link BlockCategory#SEQUENCE},
     *                  {@link BlockCategory#REPETITION}, or {@link BlockCategory#SELECTION}
     * @param blockName the class name of the block to be created
     * @return the list of blocks whose first element is an if-end block
     *                  if block type is {@link BlockCategory#SELECTION} or
     *                  {@link BlockCategory#REPETITION}
     * @exception RuntimeException if <code>blockName</code> is not supported block class name
     * @exception IllegalArgumentException if <code>blockCategory</code> is none of the values in {@link BlockCategory}
     */
    @NonNull
    public static List<BlockBase> createBlocks(@BlockCategory int blockCategory, @NonNull String blockName) {

        Class<? extends BlockBase> blockClass = getClassForName(blockName);

        switch (blockCategory) {
            case SEQUENCE: {
                return createSequenceBlock(blockClass);
            }

            case REPETITION: {
                return createRepetitionBlock(blockClass);
            }

            case SELECTION: {
                return createSelectionBlock(blockClass);
            }
        }

        throw new IllegalArgumentException("blockCategory must be one of @BlockCategory; the argument was " + blockCategory);
    }

    /**
     * Returns a block instance whose class is <code>blockName</code>.
     *
     * @param blockName the class name of the block to be created
     * @return a {@link BlockBase} instance
     * @exception RuntimeException if <code>blockName</code> is not supported block class name
     */
    @NonNull
    public static BlockBase createBlock(@NonNull String blockName) {
        Class<? extends BlockBase> blockClass = getClassForName(blockName);

        return create(blockClass);
    }
}
