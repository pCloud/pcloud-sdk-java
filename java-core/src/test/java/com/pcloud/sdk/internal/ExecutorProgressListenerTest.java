/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pcloud.sdk.internal;

import com.pcloud.sdk.ProgressListener;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class ExecutorProgressListenerTest {

    private ExecutorProgressListener testListener;
    private ProgressListener delegateListener;
    private Executor targetExecutor;

    @Before
    public void setUp() throws Exception {
        delegateListener = mock(ProgressListener.class);
        targetExecutor = mock(Executor.class);
        testListener = new ExecutorProgressListener(delegateListener, targetExecutor);
    }

    @Test
    public void onProgress_CallsDelegateMethod() throws Exception {
        targetExecutor = spy(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
        testListener = new ExecutorProgressListener(delegateListener, targetExecutor);
        long done = 1, total = 1000;
        testListener.onProgress(done, total);
        verify(delegateListener).onProgress(eq(done), eq(total));
    }

    @Test
    public void onProgress_CallsDelegateMethodOnAnotherThread() throws Exception {
        testListener.onProgress(1, 1000);
        verifyZeroInteractions(delegateListener);
        verify(targetExecutor).execute(any(Runnable.class));
    }

    @Test
    public void onProgress_Schedules_Once_Until_DelegateGetsNotified() throws Exception {
        testListener.onProgress(1, 1000);
        verify(targetExecutor).execute(any(Runnable.class));
        testListener.onProgress(2, 1000);
        verifyNoMoreInteractions(targetExecutor);
    }

    @Test
    public void onProgress_Schedules_Again_After_DelegateGetsNotified() throws Exception {
        targetExecutor = spy(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
        testListener = new ExecutorProgressListener(delegateListener, targetExecutor);
        testListener.onProgress(1, 1000);
        testListener.onProgress(2, 1000);
        verify(targetExecutor, times(2)).execute(any(Runnable.class));
    }


}