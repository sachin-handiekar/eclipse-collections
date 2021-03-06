/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.strategy.immutable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import net.jcip.annotations.Immutable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.ParallelUnsortedSetIterable;
import org.eclipse.collections.impl.UnmodifiableIteratorAdapter;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.immutable.AbstractImmutableSet;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

@Immutable
final class ImmutableUnifiedSetWithHashingStrategy<T>
        extends AbstractImmutableSet<T>
        implements Serializable, BatchIterable<T>
{
    private static final long serialVersionUID = 1L;

    private final UnifiedSetWithHashingStrategy<T> delegate;

    private ImmutableUnifiedSetWithHashingStrategy(UnifiedSetWithHashingStrategy<T> delegate)
    {
        this.delegate = delegate;
    }

    public static <T> ImmutableSet<T> newSetWith(HashingStrategy<? super T> hashingStrategy, T... elements)
    {
        return new ImmutableUnifiedSetWithHashingStrategy<T>(UnifiedSetWithHashingStrategy.newSetWith(hashingStrategy, elements));
    }

    public static <T> ImmutableSet<T> newSet(HashingStrategy<? super T> hashingStrategy, Iterable<T> iterable)
    {
        return new ImmutableUnifiedSetWithHashingStrategy<T>(UnifiedSetWithHashingStrategy.newSet(hashingStrategy, iterable));
    }

    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean equals(Object other)
    {
        return this.delegate.equals(other);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.delegate.contains(object);
    }

    public Iterator<T> iterator()
    {
        return new UnmodifiableIteratorAdapter<T>(this.delegate.iterator());
    }

    public T getFirst()
    {
        return this.delegate.getFirst();
    }

    public T getLast()
    {
        return this.delegate.getLast();
    }

    public void each(Procedure<? super T> procedure)
    {
        this.delegate.forEach(procedure);
    }

    public int getBatchCount(int batchSize)
    {
        return this.delegate.getBatchCount(batchSize);
    }

    public void batchForEach(Procedure<? super T> procedure, int sectionIndex, int sectionCount)
    {
        this.delegate.batchForEach(procedure, sectionIndex, sectionCount);
    }

    private Object writeReplace()
    {
        return new ImmutableSetWithHashingStrategySerializationProxy<T>(this, this.delegate.hashingStrategy());
    }

    @Override
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return this.delegate.asParallel(executorService, batchSize);
    }
}
