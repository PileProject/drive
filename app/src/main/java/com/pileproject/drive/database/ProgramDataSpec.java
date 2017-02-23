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
package com.pileproject.drive.database;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * A specification for "program_data" table.
 *
 * @see <a href="https://github.com/yahoo/squidb">SquiDB</a>
 */
@TableModelSpec(className="ProgramData", tableName="program_data",
                tableConstraint = "FOREIGN KEY(programId) references programs(_id) ON DELETE CASCADE")
public class ProgramDataSpec {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long id;

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