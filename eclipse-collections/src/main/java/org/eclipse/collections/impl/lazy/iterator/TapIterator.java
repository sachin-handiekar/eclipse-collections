/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.iterator;

import java.util.Iterator;

import org.eclipse.collections.api.block.procedure.Procedure;

public class TapIterator<T> implements Iterator<T>
{
    private final Iterator<T> iterator;
    private final Procedure<? super T> procedure;

    public TapIterator(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        this(iterable.iterator(), procedure);
    }

    public TapIterator(Iterator<T> iterator, Procedure<? super T> procedure)
    {
        this.iterator = iterator;
        this.procedure = procedure;
    }

    public boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    public T next()
    {
        T next = this.iterator.next();
        this.procedure.value(next);
        return next;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Cannot remove from a tap iterator");
    }
}
