package at.porscheinformatik.antimapper;

import org.junit.Assert;
import org.junit.Test;

public class HintsTest
{

    private enum Flag
    {
        A,
        B,
        C
    }

    @Test
    public void containsTest()
    {
        Object[] hints = {Flag.A, Flag.C};

        Assert.assertTrue(Hints.containsHint(hints, Flag.A));
        Assert.assertFalse(Hints.containsHint(hints, Flag.B));
        Assert.assertTrue(Hints.containsHint(hints, Flag.C));
    }
}
