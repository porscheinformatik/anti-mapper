package at.porscheinformatik.antimapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

public class MergeTest extends AbstractMapperTest
{

    @Test
    public void testNull()
    {
        char[] result = merge(null, null, BOARDING_PASS);

        assertThat(result, nullValue());
    }

    @Test
    public void test()
    {
        char[] entity = "a".toCharArray();
        char[] result = merge("A", entity, BOARDING_PASS);

        assertThat(result, is("A".toCharArray()));
        assertThat(result, sameInstance(entity));
    }

    @Test
    public void testDeleted()
    {
        char[] entity = "a".toCharArray();
        char[] result = merge(null, entity, BOARDING_PASS);

        assertThat(result, is("!a".toCharArray()));
        assertThat(result, not(sameInstance(entity)));
    }

}
