package ru.zxspectrum.disassembler.concurrent;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.Wrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author maxim
 * Date: 12/30/2023
 */
@Slf4j
public class DecoderExecutor extends ThreadPoolExecutor {
    private static final int MAX_POOL_SIZE = 32;

    private final AtomicInteger counter = new AtomicInteger();

    private final CountDownLatch latch = new CountDownLatch(1);

    private static final long KEEP_ALIVE_TIME = 100L;

    private List<Throwable> errorList = Collections.synchronizedList(new LinkedList<>());

    public DecoderExecutor() {
        super(Runtime.getRuntime().availableProcessors(), MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        counter.incrementAndGet();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (counter.decrementAndGet() == 0) {
            latch.countDown();
        }
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    @NonNull
    public void execute(@NonNull Runnable r) {
        super.execute(new RunnableWrapper(r, errorList));
    }

    public Collection<Throwable> getErrors() {
        return Collections.unmodifiableList(errorList);
    }

    private static class RunnableWrapper implements Runnable {
        private final Runnable r;

        private final List<Throwable> errorList;

        private RunnableWrapper(@NonNull Runnable r, @NonNull List<Throwable> errorList) {
            this.r = r;
            this.errorList = errorList;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (Throwable t) {
                errorList.add(t);
            }
        }
    }
}
