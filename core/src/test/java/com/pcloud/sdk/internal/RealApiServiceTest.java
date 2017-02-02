package com.pcloud.sdk.internal;

import com.pcloud.sdk.ApiService;
import com.pcloud.sdk.api.ApiServiceTest;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RealApiServiceTest extends ApiServiceTest<RealApiService> {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testInstance(new RealApiService());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        testInstance().shutdown();
    }

    @Test
    public void newBuilder_ReturnsBuilderWithSameConfiguration() throws Exception {
        ApiService.Builder builder = testInstance().newBuilder();
        RealApiService newService = (RealApiService) builder.create();

        assertEquals(testInstance().callbackExecutor(), newService.callbackExecutor());
        assertEquals(testInstance().progressCallbackThreshold(), newService.progressCallbackThreshold());
        assertEquals(testInstance().connectionPool(), newService.connectionPool());
        assertEquals(testInstance().dispatcher(), newService.dispatcher());
        assertEquals(testInstance().readTimeoutMs(), newService.readTimeoutMs());
        assertEquals(testInstance().writeTimeoutMs(), newService.writeTimeoutMs());
        assertEquals(testInstance().connectTimeoutMs(), newService.connectTimeoutMs());
        assertEquals(testInstance().cache(), newService.cache());
    }
}