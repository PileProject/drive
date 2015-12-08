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

package com.pileproject.drive.execution;


import com.pileproject.drive.programming.visual.block.BlockBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * This class has the condition of execution
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public class ExecutionCondition {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public Stack<Integer> whileStack;
    public Stack<HashMap<String, Integer>> ifStack;
    public int beginningOfCurrentWhileLoop;
    public int programCount;
    public ArrayList<BlockBase> blocks;

    public ExecutionCondition() {
        whileStack = new Stack<>();
        whileStack.clear();
        ifStack = new Stack<>();
        whileStack.clear();
        beginningOfCurrentWhileLoop = -1;
    }

    /**
     * Push the index of the selection block and the result to ifStack
     *
     * @param result
     */
    public void pushSelectionResult(boolean result) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("index", programCount);    // current index
        map.put("result", result ? TRUE : FALSE);
        ifStack.push(map);
    }
}
