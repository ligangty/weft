package org.commonjava.cdi.util.weft;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * This new ExecutorService is using Java Virtual Thread instead of the concrete Thread. It uses the
 * Executors.newVirtualThreadPerTaskExecutor() as underlying ExecutorService to submit/schedule the
 * Thread running.
 * <p></p>
 * <b>Be careful:</b> because the use pattern for virtual threads is recommending to be not pooled, so
 * there is no limit for this thread executor to submit/schedule the tasks running concurrently. This
 * means you should avoid to cache and bind any expensive resource in the task to the Virtual Thread
 * when you use this ExecutorService, otherwise it may bring potential OOM issues if the concurrent running
 * tasks are increasing drastically.
 */
public class VirtualThreadWeftExecutorService implements WeftExecutorService {
    private final ExecutorService virtualThreadExecutor;
    private final String name;

    public VirtualThreadWeftExecutorService(String name) {
        this.name = name;
        ThreadFactory virtualFactory = Thread.ofVirtual().name(name).factory();
        virtualThreadExecutor = Executors.newThreadPerTaskExecutor(virtualFactory);
        System.out.printf("Executor: %s\n", virtualThreadExecutor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHealthy() {
        return false;
    }

    @Override
    public double getLoadFactor() {
        return 0.0;
    }

    @Override
    public long getCurrentLoad() {
        return 0;
    }

    @Override
    public Integer getThreadCount() {
        return null;
    }

    @Override
    public int getCorePoolSize() {
        return 0;
    }

    @Override
    public int getMaximumPoolSize() {
        return 0;
    }

    @Override
    public int getActiveCount() {
        return 0;
    }

    @Override
    public long getTaskCount() {
        return 0;
    }

    @Override
    public void shutdown() {
        virtualThreadExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return virtualThreadExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return virtualThreadExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return virtualThreadExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return virtualThreadExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return virtualThreadExecutor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return virtualThreadExecutor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return virtualThreadExecutor.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return virtualThreadExecutor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return virtualThreadExecutor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return virtualThreadExecutor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return virtualThreadExecutor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        virtualThreadExecutor.execute(command);
    }
}
