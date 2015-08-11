/*
 * Copyright (C) 2015 PILE Project, Inc <pileproject@googlegroups.com>
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
 * Limitations under the License.
 *
 */

package com.pileproject.drive.programming.visual.block.repetition;

import android.content.Context;

import com.pileproject.drive.programming.visual.block.BlockBase;


/**
 * RepetitionBlock
 * 
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 7-July-2013
 */
public abstract class RepetitionBlock extends BlockBase {
	
	public static final int FOREVER_WHILE_OFFSET = -1000;
	
	public RepetitionBlock(Context context) {
		super(context);
	}
	
	@Override
	public Class<? extends BlockBase> getKind() {
		return RepetitionBlock.class;
	}
}