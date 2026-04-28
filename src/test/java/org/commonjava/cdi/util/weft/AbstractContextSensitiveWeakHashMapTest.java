package org.commonjava.cdi.util.weft;

import jakarta.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static org.commonjava.cdi.util.weft.ContextSensitiveWeakHashMap.newSynchronizedContextSensitiveWeakHashMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

abstract class  AbstractContextSensitiveWeakHashMapTest {
    /*Inject an ExecutorService instance using @WeftManaged.*/

    protected Weld weld;

    protected WeldContainer container;

    @Before
    public void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    protected abstract ExecutorService getExecutorService();
    protected abstract Class<? extends ExecutorService> getAssertionExecutorService();

    /**
     * We create a ThreadContext in the main thread, then create a ContextSensitiveWeakHashMap
     * and a normal WeakHashMap for comparison.
     * <p>
     * Then we start a new Runnable via the ExecutorService. In this runnable, we do:
     * 1. add an entry to both maps.
     * 2. run GC to clear weak entries.
     * 3. verify that the value is available in the ContextSensitiveWeakHashMap but NOT in normal WeakHashMap.
     * <p>
     * After the execution is done, we clearContext in main thread, run GC and verify the entry
     * in ContextSensitiveWeakHashMap is cleared.
     */
    @Test
    public void run() {
        Logger logger = LoggerFactory.getLogger(getClass());

        String path = "foo/bar/bar-1.0.pom";

//        ThreadContext ctx = ThreadContext.getContext( true ); // create ctx

        /* ReentrantLock as value has not special meaning. Anything else is equally fine. */
        final Map<String, ReentrantLock> contextSensitiveWeakHashMap = newSynchronizedContextSensitiveWeakHashMap();

        final ReentrantLock parentLock =
                contextSensitiveWeakHashMap.computeIfAbsent(new String(path), k -> new ReentrantLock());

        final Map<String, ReentrantLock> weakHashMap = new WeakHashMap<>(); // normal WeakHashMap

        Exception err = null;

        try (ExecutorService weftExecutorService = this.getExecutorService();) {
            assertEquals(weftExecutorService.getClass(), this.getAssertionExecutorService());
            /* @formatter:off */
            Future<?> task = weftExecutorService.submit( () ->
            {
                logger.debug( "Start processing..." );
                ReentrantLock lock = contextSensitiveWeakHashMap.computeIfAbsent( new String( path ),
                        k -> new ReentrantLock() );

                weakHashMap.computeIfAbsent( new String( path ),
                        k -> new ReentrantLock() );

                Runtime.getRuntime().gc();

                assertEquals( parentLock, lock );
                assertNull( weakHashMap.get( new String( path ) ) );
            } );
            /* @formatter:on */
            task.get();
        } catch (Exception e) {
            logger.error("Something failed", e);
            err = e;
        } finally {
            ThreadContext.clearContext();
        }

        Runtime.getRuntime().gc();

        assertNull(contextSensitiveWeakHashMap.get(new String(path)));
        assertNull(err);
    }

    @After
    public void shutdown() {
        weld.shutdown();
    }
}
