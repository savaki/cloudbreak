package com.sequenceiq.cloudbreak.conf;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class EnvironmentValidatorTest {

    private EnvironmentValidator underTest;

    @Before
    public void setUp() {
        underTest = new EnvironmentValidator();
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://cloudbreak.sequenceiq.com");
    }

    @Test
    public void testAfterPropertiesSetShouldAcceptBasicUrls() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://cloudbreak.sequenceiq.com");
        underTest.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSetShouldAcceptBasicHttpsUrls() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "https://cloudbreak.sequenceiq.com");
        underTest.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSetShouldAcceptBasicUrlsSpecifiedAsIpAddress() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://54.77.50.115");
        underTest.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSetShouldAcceptUrlsWithPortSpecified() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://cloudbreak.sequenceiq.com:8080");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptLocalhostWithoutAScheme() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "localhost:8080");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptUrlsWithoutAScheme() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "urlwithoutascheme.com");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptLocalhost() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://localhost");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptLocalhostWithPort() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://localhost:8080");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptInternalAddresses1() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://10.0.0.1");
        underTest.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetShouldNotAcceptInternalAddresses2() throws Exception {
        ReflectionTestUtils.setField(underTest, "hostAddress", "http://192.168.0.1");
        underTest.afterPropertiesSet();
    }
}
