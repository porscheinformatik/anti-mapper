package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TransformToTreeSet extends AbstractMapperTest
{

    @Test
    public void testNullToTreeSet()
    {
        Set<String> dtos = MAPPER.transformToTreeSet(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToTreeSetOrEmpty()
    {
        Set<String> dtos = MAPPER.transformToTreeSet(null, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptySortedSet()));
    }

    @Test
    public void testToTreeSet()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = MAPPER.transformToTreeSet(entities, BOARDING_PASS);

        assertThat(dtos, is(toTreeSet(STRING_COMPARATOR, "A", "C1", "C2")));
    }

    @Test
    public void testToTreeSetUnmodifiable()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Set<String> dtos = MAPPER.transformToTreeSet(entities, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toTreeSet(STRING_COMPARATOR, "A", "C1", "C2")));

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
