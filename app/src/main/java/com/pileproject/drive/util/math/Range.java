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
package com.pileproject.drive.util.math;

/**
 * A simple class representing mathematical interval.
 *
 * @param <T> a numerical type which this class base on
 */
public class Range<T extends Comparable<T>> {
    final private T lower;
    final private T upper;

    final private boolean includesLower;
    final private boolean includesUpper;

    private Range(T lower, boolean includesLower, T upper, boolean includesUpper) {
        this.lower = lower;
        this.upper = upper;
        this.includesLower = includesLower;
        this.includesUpper = includesUpper;
    }

    /**
     * Returns an object representing an open interval (exclusive).
     *
     * @param <T> the type of the value
     * @param lower the lower bound (exclusive)
     * @param upper the upper bound (exclusive)
     * @return the {@link Range}
     */
    public static <T extends Comparable<T>> Range<T> open(T lower, T upper) {
        return new Range<>(lower, false, upper, false);
    }

    /**
     * Returns an object representing a close interval.
     *
     * @param <T> the type of bounds
     * @param lower the lower bound (inclusive)
     * @param upper the upper bound (inclusive)
     * @return the {@link Range}
     */
    public static <T extends Comparable<T>> Range<T> closed(T lower, T upper) {
        return new Range<>(lower, true, upper, true);
    }

    /**
     * Returns an object representing a open-closed interval.
     *
     * @param <T> the type of bounds
     * @param lower the lower bound (exclusive)
     * @param upper the upper bound (inclusive)
     * @return the {@link Range}
     */
    public static <T extends Comparable<T>> Range<T> openClosed(T lower, T upper) {
        return new Range<>(lower, false, upper, true);
    }

    /**
     * Returns an object representing a closed-open interval.
     *
     * @param <T> the type of bounds
     * @param lower the lower bound (inclusive)
     * @param upper the upper bound (exclusive)
     * @return the {@link Range}
     */
    public static <T extends Comparable<T>> Range<T> closedOpen(T lower, T upper) {
        return new Range<>(lower, true, upper, false);
    }

    /**
     * Checks the given value is in the interval.
     *
     * @param value the value to be checked
     * @return <code>true</code> if the value is in the interval
     */
    public boolean contains(T value) {
        boolean aboveLower = (includesLower) ? lower.compareTo(value) <= 0 : lower.compareTo(value) < 0;
        boolean belowUpper = (includesUpper) ? value.compareTo(upper) <= 0 : value.compareTo(upper) < 0;

        return aboveLower && belowUpper;
    }

    public T getLowerBound() {
        return lower;
    }

    public T getUpperBound() {
        return upper;
    }

    @Override
    public String toString() {
        String leftSideParenthesis = (includesLower) ? "[" : "(";
        String rightSideParenthesis = (includesUpper) ? "]" : "(";

        return leftSideParenthesis + lower + ", " + upper +
                rightSideParenthesis;
    }
}
