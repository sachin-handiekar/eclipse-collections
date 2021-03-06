/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.jmh.runner.AbstractJMHTestRunner;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SumOfIntTest extends AbstractJMHTestRunner
{
    private static final int SIZE = 3_000_000;
    private static final int BATCH_SIZE = 10_000;
    private static final Stream<Integer> INTEGERS = new Random().ints(0, 10_000).boxed();

    private final List<Integer> integersJDK = INTEGERS.limit(SIZE).collect(Collectors.toList());
    private final MutableList<Integer> integersEC = FastList.newListWith(this.integersJDK.toArray(new Integer[SIZE]));

    private ExecutorService executorService;

    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public int serial_lazy_collectIntSum_jdk()
    {
        return this.integersJDK.stream().mapToInt(each -> each).sum();
    }

    @Benchmark
    public int serial_lazy_collectIntSum_streams_ec()
    {
        return this.integersEC.stream().mapToInt(each -> each).sum();
    }

    @Benchmark
    public long serial_lazy_collectLongSum_jdk()
    {
        return this.integersJDK.stream().mapToLong(each -> each).sum();
    }

    @Benchmark
    public long serial_lazy_collectLongSum_streams_ec()
    {
        return this.integersEC.stream().mapToLong(each -> each).sum();
    }

    @Benchmark
    public int parallel_lazy_collectIntSum_jdk()
    {
        return this.integersJDK.parallelStream().mapToInt(Integer::intValue).sum();
    }

    @Benchmark
    public int parallel_lazy_collectIntSum_streams_ec()
    {
        return this.integersEC.parallelStream().mapToInt(Integer::intValue).sum();
    }

    @Benchmark
    public long parallel_lazy_collectLongSum_jdk()
    {
        return this.integersJDK.parallelStream().mapToLong(Integer::longValue).sum();
    }

    @Benchmark
    public long parallel_lazy_collectLongSum_streams_ec()
    {
        return this.integersEC.parallelStream().mapToLong(Integer::longValue).sum();
    }

    @Benchmark
    public long serial_eager_directSumOfInt_ec()
    {
        return this.integersEC.sumOfInt(each -> each);
    }

    @Benchmark
    public long serial_eager_collectIntSum_ec()
    {
        return this.integersEC.collectInt(each -> each).sum();
    }

    @Benchmark
    public long serial_lazy_collectIntSum_ec()
    {
        return this.integersEC.asLazy().collectInt(each -> each).sum();
    }

    @Benchmark
    public long parallel_lazy_directSumOfInt_ec()
    {
        return this.integersEC.asParallel(this.executorService, BATCH_SIZE).sumOfInt(each -> each);
    }

    @Benchmark
    public long serial_lazy_directSumOfInt_ec()
    {
        return this.integersEC.asLazy().sumOfInt(each -> each);
    }
}
