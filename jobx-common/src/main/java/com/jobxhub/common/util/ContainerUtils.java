/**
 * Copyright (c) 2015 The JobX Project
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jobxhub.common.util;

import com.jobxhub.common.util.collection.NonBlockingHashMap;
import com.jobxhub.common.util.collection.NonBlockingHashMapLong;

import java.util.*;
import com.jobxhub.common.util.collection.HashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jobxhub.common.util.AssertUtils.checkArgument;
import static com.jobxhub.common.util.AssertUtils.checkNotNull;

/**
 * Static utility methods pertaining to {@link List} instances.
 */
public final class ContainerUtils {

    /**
     * Creates a mutable, empty {@code ArrayList} instance.
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * Creates a mutable {@code ArrayList} instance containing the given elements.
     */
    @SuppressWarnings("unchecked")
    public static <E> ArrayList<E> newArrayList(E... elements) {
        checkNotNull(elements);
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * Creates a mutable {@code ArrayList} instance containing the given elements.
     */
    @SuppressWarnings("unchecked")
    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        checkNotNull(elements);
        return elements instanceof Collection
                ? new ArrayList((Collection<E>) elements)
                : newArrayList(elements.iterator());
    }

    /**
     * Creates a mutable {@code ArrayList} instance containing the given elements.
     */
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }

    /**
     * Creates an {@code ArrayList} instance backed by an array of the exact size specified;
     * equivalent to {@link ArrayList#ArrayList(int)}.
     */
    public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
        checkArgument(initialArraySize >= 0);
        return new ArrayList<E>(initialArraySize);
    }

    abstract static class TransformedIterator<F, T> implements Iterator<T> {
        final Iterator<? extends F> backingIterator;

        TransformedIterator(Iterator<? extends F> backingIterator) {
            this.backingIterator = checkNotNull(backingIterator);
        }

        abstract T transform(F from);

        @Override
        public final boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public final T next() {
            return transform(backingIterator.next());
        }

        @Override
        public final void remove() {
            backingIterator.remove();
        }
    }

    abstract static class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T> {

        TransformedListIterator(ListIterator<? extends F> backingIterator) {
            super(backingIterator);
        }

        @SuppressWarnings("unchecked")
        private ListIterator<? extends F> backingIterator() {
            return (ListIterator<? extends F>) backingIterator;
        }

        @Override
        public final boolean hasPrevious() {
            return backingIterator().hasPrevious();
        }

        @Override
        public final T previous() {
            return transform(backingIterator().previous());
        }

        @Override
        public final int nextIndex() {
            return backingIterator().nextIndex();
        }

        @Override
        public final int previousIndex() {
            return backingIterator().previousIndex();
        }

        @Override
        public void set(T element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T element) {
            throw new UnsupportedOperationException();
        }
    }

    static int computeArrayListCapacity(int arraySize) {
        checkArgument(arraySize >= 0);
        return IntUtils.saturatedCast(5L + arraySize + (arraySize / 10));
    }


    /**
     * Creates a mutable, empty {@code HashMap} instance.
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * Creates a {@code HashMap} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<K, V>(capacity(expectedSize));
    }

    /**
     * Creates an {@code IdentityHashMap} instance.
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }

    /**
     * Creates an {@code IdentityHashMap} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMapWithExpectedSize(int expectedSize) {
        return new IdentityHashMap<K, V>(capacity(expectedSize));
    }

    /**
     * Creates a mutable, empty, insertion-ordered {@code LinkedHashMap} instance.
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * Creates a mutable, empty {@code TreeMap} instance using the natural ordering of its elements.
     */
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    /**
     * Creates a mutable, empty {@code ConcurrentMap} instance.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(boolean useNonBlocking) {
        if (useNonBlocking) {
            return new NonBlockingHashMap();
        }
        return new HashMap<K, V>();
    }

    /**
     * Creates a {@code ConcurrentMap} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int initialCapacity, boolean useNonBlocking) {
        if (useNonBlocking) {
            return new NonBlockingHashMap<K, V>(initialCapacity);
        }
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * Creates a mutable, empty {@code NonBlockingHashMapLong} instance.
     */
    public static <V> ConcurrentMap<Long, V> newConcurrentMapLong() {
        return new NonBlockingHashMapLong();
    }

    /**
     * Creates a {@code NonBlockingHashMapLong} instance, with a high enough "initial capacity"
     * that it should hold {@code expectedSize} elements without growth.
     */
    public static <V> ConcurrentMap<Long, V> newConcurrentMapLong(int initialCapacity) {
        return new NonBlockingHashMapLong(initialCapacity);
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as
     * long as it grows no larger than expectedSize and the load factor is >= its
     * default (0.75).
     */
    private static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            checkArgument(expectedSize >= 0, "expectedSize cannot be negative but was: " + expectedSize);
            return expectedSize + 1;
        }
        if (expectedSize < IntUtils.MAX_POWER_OF_TWO) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE; // any large value
    }
}
