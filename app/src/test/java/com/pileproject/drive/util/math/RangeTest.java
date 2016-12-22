/**
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
package com.pileproject.drive.util.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void testOpen() throws Exception {
        Range<Integer> open = Range.open(0, 2);

        assertFalse(open.contains(0));
        assertTrue(open.contains(1));
        assertFalse(open.contains(2));
    }

    @Test
    public void testClosed() throws Exception {
        Range<Integer> closed = Range.closed(0, 2);

        assertFalse(closed.contains(-1));
        assertTrue(closed.contains(0));
        assertTrue(closed.contains(1));
        assertTrue(closed.contains(2));
        assertFalse(closed.contains(3));
    }

    @Test
    public void testOpenClosed() throws Exception {
        Range<Integer> openClosed = Range.openClosed(0, 2);

        assertFalse(openClosed.contains(-1));
        assertFalse(openClosed.contains(0));
        assertTrue(openClosed.contains(1));
        assertTrue(openClosed.contains(2));
        assertFalse(openClosed.contains(3));
    }

    @Test
    public void testClosedOpen() throws Exception {
        Range<Integer> closedOpen = Range.closedOpen(0, 2);

        assertTrue(closedOpen.contains(0));
        assertTrue(closedOpen.contains(1));
        assertFalse(closedOpen.contains(2));
    }
}