package com.sebastiandagostino.callcenter.domain;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CallTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCallCreationWithInvalidParameter() {
        new Call(-1);
    }

    @Test(expected = NullPointerException.class)
    public void testCallCreationWithNullParameter() {
        new Call(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCallCreationWithInvalidFirstParameter() {
        Call.buildRandomCall(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCallCreationWithInvalidSecondParameter() {
        Call.buildRandomCall(1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCallCreationWithInvalidParameterOrder() {
        Call.buildRandomCall(2, 1);
    }

    @Test
    public void testRandomCallCreationWithValidParameters() {
        Integer min = 5;
        Integer max = 10;
        Call call = Call.buildRandomCall(min, max);

        assertNotNull(call);
        assertTrue(min <= call.getDurationInSeconds());
        assertTrue(call.getDurationInSeconds() <= max);
    }

}
