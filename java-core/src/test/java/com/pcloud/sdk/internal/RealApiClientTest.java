package com.pcloud.sdk.internal;

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiServiceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RealApiClientTest extends ApiServiceTest<RealApiClient> {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testInstance(new RealApiClient());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        testInstance().shutdown();
    }

    @Test
    public void newBuilder_ReturnsBuilderWithSameConfiguration() throws Exception {
        ApiClient.Builder builder = testInstance().newBuilder();
        RealApiClient newService = (RealApiClient) builder.create();

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