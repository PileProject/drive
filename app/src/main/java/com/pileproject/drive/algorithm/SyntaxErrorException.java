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

import com.pileproject.drive.programming.visual.block.BlockBase;

@SuppressWarnings("serial")
public class SyntaxErrorException extends Exception {
    private BlockBase mErrorblock;
    private ErrorCode mErrorcode;

    public enum ErrorCode {
        NESTED_IF,
        END_BEFORE_BEGIN_FOR,
        END_BEFORE_BEGIN_IF,
        UNEXPECTED_BREAK,
        NO_END_FOR,
        NO_END_IF,
        OTHER  // TODO: find other reasons for syntax errors
    }

    public SyntaxErrorException(BlockBase error, ErrorCode code) {
        mErrorblock = error;
        mErrorcode = code;
    }

    public BlockBase getBlock() {
        return mErrorblock;
    }

    public ErrorCode getErrorCode() {
        return mErrorcode;
    }
}
