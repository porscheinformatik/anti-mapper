package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TransformToHashSet extends AbstractMapperTest
{

    @Test
    public void testNullToHashSet()
    {
        Set<String> dtos = MAPPER.transformToHashSet(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testToHashSet()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = MAPPER.transformToHashSet(entities, BOARDING_PASS);

        assertThat(dtos, is(toSet("A", "C1", "C2")));
    }

    @Test
    public void testToHashSetKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = MAPPER.transformToHashSet(entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toSet("A", null, "C1", "C2")));

        try
        {
            dtos.add("Z");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
