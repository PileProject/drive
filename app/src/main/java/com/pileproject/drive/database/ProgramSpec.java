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
import com.yahoo.squidb.annotations.Constants;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * A specification for "programs" table.
 *
 * @see <a href="https://github.com/yahoo/squidb">SquiDB</a>
 */
@TableModelSpec(className="Program", tableName="programs")
public class ProgramSpec {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long id;

    // the name of a program
    @ColumnSpec(constraints = "NOT NULL")
    String name;

    // the type of a program; "execution", "user" or "sample"
    @ColumnSpec(constraints = "NOT NULL")
    String type;

    // the time of creation
    @ColumnSpec(constraints = "NOT NULL")
    long updatedAt;

    @Constants
    public static class TypeConst {
        public static final String EXECUTION = "execution";
        public static final String USER = "user";
        public static final String SAMPLE = "sample";
    }
}
