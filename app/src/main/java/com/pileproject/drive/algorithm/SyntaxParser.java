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

import com.pileproject.drive.programming.visual.activity.BlockPositionComparator;
import com.pileproject.drive.programming.visual.block.BlockBase;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionBlock;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionBreakBlock;
import com.pileproject.drive.programming.visual.block.repetition.RepetitionEndBlock;
import com.pileproject.drive.programming.visual.block.selection.SelectionBlock;
import com.pileproject.drive.programming.visual.block.selection.SelectionEndBlock;
import com.pileproject.drive.programming.visual.block.sequence.SequenceBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.pileproject.drive.algorithm.SyntaxErrorException.ErrorCode;

public class SyntaxParser {
    public static ProgramSyntaxTree parseProgram(ArrayList<BlockBase> blocks) throws SyntaxErrorException {
        ProgramSyntaxTree program = new ProgramSyntaxTree();

        Collections.sort(blocks, new BlockPositionComparator());
        Iterator<BlockBase> it = blocks.iterator();
        while (it.hasNext()) {
            program.addStatement(parseStatement(it));
        }
        return program;
    }

    private static SyntaxTreeBase parseStatement(Iterator<BlockBase> blockiter) throws SyntaxErrorException {
        BlockBase curblock = blockiter.next();
        Class<? extends BlockBase> c = curblock.getKind();
        if (c == SequenceBlock.class) {
            return new SequenceSyntaxTree(curblock);
        } else if (c == RepetitionBlock.class) {
            return parseRepetition(blockiter, curblock);
        } else if (c == SelectionBlock.class) {
            return parseSelection(blockiter, curblock, false);
        } else {
            ErrorCode code;
            if (c == RepetitionBreakBlock.class) {
                code = ErrorCode.UNEXPECTED_BREAK;
            } else if (c == RepetitionEndBlock.class) {
                code = ErrorCode.END_BEFORE_BEGIN_FOR;
            } else if (c == SelectionEndBlock.class) {
                code = ErrorCode.END_BEFORE_BEGIN_IF;
            } else {
                code = ErrorCode.OTHER;
            }
            throw new SyntaxErrorException(curblock, code);
        }
    }

    private static RepetitionSyntaxTree parseRepetition(Iterator<BlockBase> blockiter, BlockBase defblock)
            throws SyntaxErrorException {
        ArrayList<SyntaxTreeBase> body = new ArrayList<>();
        try {
            BlockBase curblock = blockiter.next();
            while (!(curblock instanceof RepetitionEndBlock)) {
                Class<? extends BlockBase> c = curblock.getKind();
                if (c == SequenceBlock.class) {
                    body.add(new SequenceSyntaxTree(curblock));
                } else if (c == RepetitionBlock.class) {
                    body.add(parseRepetition(blockiter, curblock));
                } else if (c == RepetitionBreakBlock.class) {
                    body.add(new SequenceSyntaxTree(curblock));
                } else if (c == SelectionBlock.class) {
                    body.add(parseSelection(blockiter, curblock, true));
                } else {
                    ErrorCode code;
                    if (c == SelectionEndBlock.class) {
                        code = ErrorCode.END_BEFORE_BEGIN_IF;
                    } else {
                        code = ErrorCode.OTHER;
                    }
                    throw new SyntaxErrorException(curblock, code);
                }
                curblock = blockiter.next();
            }
            // now curblock points RepetitionEndBlock
            return new RepetitionSyntaxTree(defblock, curblock, body);
        } catch (NoSuchElementException noex) {
            throw new SyntaxErrorException(defblock, ErrorCode.NO_END_FOR);
        }
    }

    private static SelectionSyntaxTree parseSelection(
            Iterator<BlockBase> blockiter,
            BlockBase defblock,
            boolean inwhile) throws SyntaxErrorException {
        ArrayList<SyntaxTreeBase> truebody;
        ArrayList<SyntaxTreeBase> falsebody;
        ArrayList<BlockBase> truebodyBlocks = new ArrayList<>();
        ArrayList<BlockBase> falsebodyBlocks = new ArrayList<>();

        int threshold = defblock.getRight();
        try {
            BlockBase curblock = blockiter.next();
            while (!(curblock instanceof SelectionEndBlock)) {
                if (curblock.getLeft() > threshold) {
                    truebodyBlocks.add(curblock);
                } else {
                    falsebodyBlocks.add(curblock);
                }
                curblock = blockiter.next();
            }
            // now curblock points SelectionEndBlock
            truebody = parseSelectionBody(truebodyBlocks, inwhile);
            falsebody = parseSelectionBody(falsebodyBlocks, inwhile);
            return new SelectionSyntaxTree(defblock, curblock, truebody, falsebody);
        } catch (NoSuchElementException noex) {
            throw new SyntaxErrorException(defblock, ErrorCode.NO_END_IF);
        }
    }

    private static ArrayList<SyntaxTreeBase> parseSelectionBody(ArrayList<BlockBase> blocks, boolean inwhile)
            throws SyntaxErrorException {
        ArrayList<SyntaxTreeBase> body = new ArrayList<>();
        Iterator<BlockBase> it = blocks.iterator();
        while (it.hasNext()) {
            BlockBase curblock = it.next();
            Class<? extends BlockBase> c = curblock.getKind();
            if (c == SequenceBlock.class) {
                body.add(new SequenceSyntaxTree(curblock));
            } else if (c == RepetitionBlock.class) {
                body.add(parseRepetition(it, curblock));
            } else if (c == RepetitionBreakBlock.class && inwhile == true) {
                body.add(new SequenceSyntaxTree(curblock));
            } else {
                ErrorCode code;
                if (c == RepetitionBreakBlock.class) {
                    code = ErrorCode.UNEXPECTED_BREAK;
                } else if (c == RepetitionEndBlock.class) {
                    code = ErrorCode.END_BEFORE_BEGIN_FOR;
                } else if (c == SelectionBlock.class) {
                    code = ErrorCode.NESTED_IF;
                } else {
                    code = ErrorCode.OTHER;
                }
                throw new SyntaxErrorException(curblock, code);
            }
        }
        return body;
    }
}
