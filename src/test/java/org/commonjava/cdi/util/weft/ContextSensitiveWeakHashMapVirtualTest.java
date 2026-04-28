/**
 * Copyright (C) 2013-2022 Red Hat, Inc. (https://github.com/Commonjava/weft)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.cdi.util.weft;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.ExecutorService;

@ApplicationScoped
public class ContextSensitiveWeakHashMapVirtualTest extends AbstractContextSensitiveWeakHashMapTest {
    /*Inject an ExecutorService instance using @WeftManaged.*/
    @WeftManaged
    @Inject
    @VirtualThreadExecutor(named="virtual-test")
    private ExecutorService executor;

    ExecutorService getExecutor() {
        return executor;
    }

    @Override
    protected ExecutorService getExecutorService() {
        return container.select(ContextSensitiveWeakHashMapVirtualTest.class).get().getExecutor();
    }

    @Override
    protected Class<? extends ExecutorService> getAssertionExecutorService() {
        return VirtualThreadWeftExecutorService.class;
    }
}
