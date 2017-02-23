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

import java.util.List;

/**
 * An interface for providing {@link Class} instances of {@link BlockBase} and its descendant classes.
 * Concrete classes are supposed to provide the concrete {@link Class} instances with respect to
 * their responsible robots.
 *
 */
public interface BlockProvider {

    /**
     * Returns all available sequence block classes.
     *
     * @return a list of sequence block classes
     */
    List<Class<? extends BlockBase>> getSequenceBlockClasses();

    /**
     * Returns all available selection block classes.
     *
     * @return a list of selection block classes
     */
    List<Class<? extends BlockBase>> getSelectionBlockClasses();

    /**
     * Returns all available repetition block classes.
     *
     * @return a list of repetition block classes
     */
    List<Class<? extends BlockBase>> getRepetitionBlockClasses();
}
