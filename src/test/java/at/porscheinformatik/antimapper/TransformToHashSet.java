package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TransformToHashSet extends AbstractMapperTest
{

    @Test
    public void testNullToHashSet()
    {
        Set<String> dtos = transformAll((List<char[]>) null, BOARDING_PASS).toHashSet();

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToHashSetOrEmpty()
    {
        Set<String> dtos = transformAll((List<char[]>) null, BOARDING_PASS, Hint.OR_EMPTY).toHashSet();

        assertThat(dtos, is(Collections.emptySet()));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToHashSet()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = transformAll(entities, BOARDING_PASS).toHashSet();

        assertThat(dtos, is(toSet("A", "C1", "C2")));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToHashSetKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = transformAll(entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).toHashSet();

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
