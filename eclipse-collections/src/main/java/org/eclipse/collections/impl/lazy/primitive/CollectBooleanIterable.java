/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.primitive;

import java.util.Iterator;

import net.jcip.annotations.Immutable;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.primitive.BooleanPredicate;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.BooleanProcedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.set.mutable.primitive.BooleanHashSet;

/**
 * A CollectIntIterable is an iterable that transforms a source iterable using an IntFunction as it iterates.
 */
@Immutable
public class CollectBooleanIterable<T>
        extends AbstractLazyBooleanIterable
{
    private final LazyIterable<T> iterable;
    private final BooleanFunction<? super T> function;
    private final BooleanFunctionToProcedure<T> booleanFunctionToProcedure;

    public CollectBooleanIterable(LazyIterable<T> adapted, BooleanFunction<? super T> function)
    {
        this.iterable = adapted;
        this.function = function;
        this.booleanFunctionToProcedure = new BooleanFunctionToProcedure<T>(function);
    }

    public BooleanIterator booleanIterator()
    {
        return new BooleanIterator()
        {
            private final Iterator<T> iterator = CollectBooleanIterable.this.iterable.iterator();

            public boolean next()
            {
                return CollectBooleanIterable.this.function.booleanValueOf(this.iterator.next());
            }

            public boolean hasNext()
            {
                return this.iterator.hasNext();
            }
        };
    }

    public void forEach(BooleanProcedure procedure)
    {
        this.each(procedure);
    }

    /**
     * @since 7.0.
     */
    public void each(BooleanProcedure procedure)
    {
        this.iterable.forEachWith(this.booleanFunctionToProcedure, procedure);
    }

    @Override
    public int size()
    {
        return this.iterable.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.iterable.isEmpty();
    }

    @Override
    public boolean notEmpty()
    {
        return this.iterable.notEmpty();
    }

    @Override
    public int count(final BooleanPredicate predicate)
    {
        return this.iterable.count(new Predicate<T>()
        {
            public boolean accept(T each)
            {
                return predicate.accept(CollectBooleanIterable.this.function.booleanValueOf(each));
            }
        });
    }

    @Override
    public boolean anySatisfy(final BooleanPredicate predicate)
    {
        return this.iterable.anySatisfy(new Predicate<T>()
        {
            public boolean accept(T each)
            {
                return predicate.accept(CollectBooleanIterable.this.function.booleanValueOf(each));
            }
        });
    }

    @Override
    public boolean allSatisfy(final BooleanPredicate predicate)
    {
        return this.iterable.allSatisfy(new Predicate<T>()
        {
            public boolean accept(T each)
            {
                return predicate.accept(CollectBooleanIterable.this.function.booleanValueOf(each));
            }
        });
    }

    @Override
    public boolean noneSatisfy(final BooleanPredicate predicate)
    {
        return this.iterable.allSatisfy(new Predicate<T>()
        {
            public boolean accept(T each)
            {
                return !predicate.accept(CollectBooleanIterable.this.function.booleanValueOf(each));
            }
        });
    }

    @Override
    public boolean[] toArray()
    {
        final boolean[] array = new boolean[this.size()];
        this.iterable.forEachWithIndex(new ObjectIntProcedure<T>()
        {
            public void value(T each, int index)
            {
                array[index] = CollectBooleanIterable.this.function.booleanValueOf(each);
            }
        });
        return array;
    }

    @Override
    public MutableBooleanList toList()
    {
        return BooleanArrayList.newList(this);
    }

    @Override
    public MutableBooleanSet toSet()
    {
        return BooleanHashSet.newSet(this);
    }

    @Override
    public MutableBooleanBag toBag()
    {
        return BooleanHashBag.newBag(this);
    }

    @Override
    public boolean containsAll(boolean... source)
    {
        for (boolean value : source)
        {
            if (!this.contains(value))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(BooleanIterable source)
    {
        for (BooleanIterator iterator = source.booleanIterator(); iterator.hasNext(); )
        {
            if (!this.contains(iterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    private static final class BooleanFunctionToProcedure<T> implements Procedure2<T, BooleanProcedure>
    {
        private static final long serialVersionUID = 1L;
        private final BooleanFunction<? super T> function;

        private BooleanFunctionToProcedure(BooleanFunction<? super T> function)
        {
            this.function = function;
        }

        public void value(T each, BooleanProcedure procedure)
        {
            procedure.value(this.function.booleanValueOf(each));
        }
    }
}
