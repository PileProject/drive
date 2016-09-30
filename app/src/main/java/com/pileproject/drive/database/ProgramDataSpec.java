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

package com.pileproject.drive.database;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Specification for "program_data" table
 *
 * @author <a href="mailto:tatsuyaw0c@gmail.com">Tatsuya Iwanari</a>
 * @version 1.0 22-March-2016
 */
@TableModelSpec(className="ProgramData", tableName="program_data",
                tableConstraint = "FOREIGN KEY(programId) references programs(_id) ON DELETE CASCADE")
public class ProgramDataSpec {
    // _id will be generated automatically

    @ColumnSpec(constraints = "NOT NULL")
    long programId;

    // the type (name) of block
    @ColumnSpec(constraints = "NOT NULL")
    String type;

    // the x position of the left of a block
    @ColumnSpec(constraints = "NOT NULL")
    int left;

    // the y position of the top of a block
    @ColumnSpec(constraints = "NOT NULL")
    int top;

    // the number which a number holder block has
    @ColumnSpec(defaultValue = "0")
    String number;
}