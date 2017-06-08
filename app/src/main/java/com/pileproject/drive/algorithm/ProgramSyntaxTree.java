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

import java.util.ArrayList;

public class ProgramSyntaxTree extends SyntaxTreeBase {
    private static final int LEFT_PADDING_DP = 50;
    private static final int TOP_PADDING_DP = 50;
    protected static final int BLOCK_MARGIN_DP = 30;

    public ArrayList<SyntaxTreeBase> body;

    public ProgramSyntaxTree() {
        body = new ArrayList<>();
    }

    public void addStatement(SyntaxTreeBase s) {
        body.add(s);
    }

    @Override
    protected int align(Resources res, int top, int left, int margin) {
        for (SyntaxTreeBase node : body) {
            top = node.align(res, top, left, margin);
        }
        return top;
    }

    public void alignment(Resources res, float dpfactor) {
        int toppx = (int) (TOP_PADDING_DP * dpfactor + 0.5f);
        int leftpx = (int) (LEFT_PADDING_DP * dpfactor + 0.5f);
        int marginpx = (int) (BLOCK_MARGIN_DP * dpfactor + 0.5f);
        this.align(res, toppx, leftpx, marginpx);
    }

    public static String bodyToString(ArrayList<SyntaxTreeBase> body) {
        String code = "";
        for (SyntaxTreeBase stmt : body) {
            Class<?> c = stmt.getClass();
            if (c == SequenceSyntaxTree.class) {
                code += "sequence " + ((SequenceSyntaxTree) stmt).getDefinition().getClass().getSimpleName() + "\n";
            } else if (c == RepetitionSyntaxTree.class) {
                int numrep = ((RepetitionSyntaxTree) stmt).getRepetitionNum();
                if (numrep == -1) {
                    code += "repetition forever {\n";
                } else {
                    code += "repetition " + numrep + " times {\n";
                }
                code += bodyToString(((RepetitionSyntaxTree) stmt).body);
                code += "}\n";
            } else if (c == SelectionSyntaxTree.class) {
                code += "selection:\ntrue {\n";
                code += bodyToString(((SelectionSyntaxTree) stmt).truebody);
                code += "} false {\n";
                code += bodyToString(((SelectionSyntaxTree) stmt).falsebody);
                code += "}\n";
            } else {
                code += "no such class\n";
            }
        }
        return code;
    }

    public String toString() {
        return bodyToString(body);
    }
}
