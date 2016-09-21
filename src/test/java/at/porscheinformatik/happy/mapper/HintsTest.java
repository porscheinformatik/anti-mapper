package at.porscheinformatik.happy.mapper;

import junit.framework.Assert;

public class HintsTest
{

    private enum Flag
    {
        A,
        B,
        C
    }

    public void containsTest()
    {
        Object[] hints = {Flag.A, Flag.C};

        Assert.assertTrue(Hints.containsHint(hints, Flag.A));
        Assert.assertFalse(Hints.containsHint(hints, Flag.B));
        Assert.assertTrue(Hints.containsHint(hints, Flag.C));
    }
}
