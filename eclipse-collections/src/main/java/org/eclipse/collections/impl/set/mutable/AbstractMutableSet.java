/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.set.PartitionMutableSet;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.set.UnsortedSetIterable;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectByteProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectCharProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectLongProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectShortProcedure;
import org.eclipse.collections.impl.collection.mutable.AbstractMutableCollection;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.partition.set.PartitionUnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.ByteHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.CharHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.DoubleHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.FloatHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.eclipse.collections.impl.set.mutable.primitive.ShortHashSet;
import org.eclipse.collections.impl.utility.internal.SetIterables;
import org.eclipse.collections.impl.utility.internal.SetIterate;

public abstract class AbstractMutableSet<T>
        extends AbstractMutableCollection<T>
        implements MutableSet<T>
{
    @Override
    public MutableSet<T> clone()
    {
        try
        {
            return (MutableSet<T>) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError(e);
        }
    }

    public MutableSet<T> newEmpty()
    {
        return UnifiedSet.newSet();
    }

    protected <K> MutableSet<K> newEmptySameSize()
    {
        return UnifiedSet.newSet(this.size());
    }

    public MutableSet<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    public MutableSet<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, this.newEmpty());
    }

    public <P> MutableSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.selectWith(predicate, parameter, this.<T>newEmptySameSize());
    }

    public MutableSet<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, this.<T>newEmptySameSize());
    }

    public <P> MutableSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.rejectWith(predicate, parameter, this.<T>newEmptySameSize());
    }

    public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSet<T> partitionMutableSet = new PartitionUnifiedSet<T>();
        this.forEach(new PartitionProcedure<T>(predicate, partitionMutableSet));
        return partitionMutableSet;
    }

    public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSet<T> partitionMutableSet = new PartitionUnifiedSet<T>();
        this.forEach(new PartitionPredicate2Procedure<T, P>(predicate, parameter, partitionMutableSet));
        return partitionMutableSet;
    }

    public <S> MutableSet<S> selectInstancesOf(Class<S> clazz)
    {
        MutableSet<S> result = (MutableSet<S>) this.newEmpty();
        this.forEach(new SelectInstancesOfProcedure<S>(clazz, result));
        return result;
    }

    public <V> MutableSet<V> collect(Function<? super T, ? extends V> function)
    {
        return this.collect(function, this.<V>newEmptySameSize());
    }

    public MutableBooleanSet collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        MutableBooleanSet result = new BooleanHashSet();
        this.forEach(new CollectBooleanProcedure<T>(booleanFunction, result));
        return result;
    }

    public MutableByteSet collectByte(ByteFunction<? super T> byteFunction)
    {
        MutableByteSet result = new ByteHashSet();
        this.forEach(new CollectByteProcedure<T>(byteFunction, result));
        return result;
    }

    public MutableCharSet collectChar(CharFunction<? super T> charFunction)
    {
        MutableCharSet result = new CharHashSet(this.size());
        this.forEach(new CollectCharProcedure<T>(charFunction, result));
        return result;
    }

    public MutableDoubleSet collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        MutableDoubleSet result = new DoubleHashSet(this.size());
        this.forEach(new CollectDoubleProcedure<T>(doubleFunction, result));
        return result;
    }

    public MutableFloatSet collectFloat(FloatFunction<? super T> floatFunction)
    {
        MutableFloatSet result = new FloatHashSet(this.size());
        this.forEach(new CollectFloatProcedure<T>(floatFunction, result));
        return result;
    }

    public MutableIntSet collectInt(IntFunction<? super T> intFunction)
    {
        MutableIntSet result = new IntHashSet(this.size());
        this.forEach(new CollectIntProcedure<T>(intFunction, result));
        return result;
    }

    public MutableLongSet collectLong(LongFunction<? super T> longFunction)
    {
        MutableLongSet result = new LongHashSet(this.size());
        this.forEach(new CollectLongProcedure<T>(longFunction, result));
        return result;
    }

    public MutableShortSet collectShort(ShortFunction<? super T> shortFunction)
    {
        MutableShortSet result = new ShortHashSet(this.size());
        this.forEach(new CollectShortProcedure<T>(shortFunction, result));
        return result;
    }

    public <V> MutableSet<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.flatCollect(function, this.<V>newEmptySameSize());
    }

    public <P, V> MutableSet<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collectWith(function, parameter, this.<V>newEmptySameSize());
    }

    public <V> MutableSet<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.collectIf(predicate, function, this.<V>newEmptySameSize());
    }

    public <V> UnifiedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, UnifiedSetMultimap.<V, T>newMultimap());
    }

    public <V> UnifiedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, UnifiedSetMultimap.<V, T>newMultimap());
    }

    public MutableSet<T> asUnmodifiable()
    {
        return UnmodifiableMutableSet.of(this);
    }

    public MutableSet<T> asSynchronized()
    {
        return SynchronizedMutableSet.of(this);
    }

    public ImmutableSet<T> toImmutable()
    {
        return Sets.immutable.withAll(this);
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    public <S> MutableSet<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.zip(that, this.<Pair<T, S>>newEmptySameSize());
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    public MutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(this.<Pair<T, Integer>>newEmptySameSize());
    }

    @Override
    public boolean removeAllIterable(Iterable<?> iterable)
    {
        return SetIterate.removeAllIterable(this, iterable);
    }

    public MutableSet<T> union(SetIterable<? extends T> set)
    {
        return SetIterables.union(this, set);
    }

    public <R extends Set<T>> R unionInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.unionInto(this, set, targetSet);
    }

    public MutableSet<T> intersect(SetIterable<? extends T> set)
    {
        return SetIterables.intersect(this, set);
    }

    public <R extends Set<T>> R intersectInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.intersectInto(this, set, targetSet);
    }

    public MutableSet<T> difference(SetIterable<? extends T> subtrahendSet)
    {
        return SetIterables.difference(this, subtrahendSet);
    }

    public <R extends Set<T>> R differenceInto(SetIterable<? extends T> subtrahendSet, R targetSet)
    {
        return SetIterables.differenceInto(this, subtrahendSet, targetSet);
    }

    public MutableSet<T> symmetricDifference(SetIterable<? extends T> setB)
    {
        return SetIterables.symmetricDifference(this, setB);
    }

    public <R extends Set<T>> R symmetricDifferenceInto(SetIterable<? extends T> set, R targetSet)
    {
        return SetIterables.symmetricDifferenceInto(this, set, targetSet);
    }

    public boolean isSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isSubsetOf(this, candidateSuperset);
    }

    public boolean isProperSubsetOf(SetIterable<? extends T> candidateSuperset)
    {
        return SetIterables.isProperSubsetOf(this, candidateSuperset);
    }

    public MutableSet<UnsortedSetIterable<T>> powerSet()
    {
        return (MutableSet<UnsortedSetIterable<T>>) (MutableSet<?>) SetIterables.powerSet(this);
    }

    public <B> LazyIterable<Pair<T, B>> cartesianProduct(SetIterable<B> set)
    {
        return SetIterables.cartesianProduct(this, set);
    }
}
