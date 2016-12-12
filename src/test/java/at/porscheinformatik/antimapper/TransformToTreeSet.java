package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.junit.Test;

public class TransformToTreeSet extends AbstractMapperTest
{

    @Test
    public void testNullToTreeSet()
    {
        SortedSet<String> dtos = MAPPER.transformToTreeSet(null, BOARDING_PASS, STRING_COMPARATOR);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToTreeSetOrEmpty()
    {
        SortedSet<String> dtos = MAPPER.transformToTreeSet(null, STRING_COMPARATOR, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptySortedSet()));
        assertThat(dtos.comparator(), is(STRING_COMPARATOR));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToTreeSet()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        SortedSet<String> dtos = MAPPER.transformToTreeSet(entities, STRING_COMPARATOR, BOARDING_PASS);

        assertThat(dtos, is(toSortedSet(STRING_COMPARATOR, "A", "C1", "C2")));
        assertThat(dtos.comparator(), is(STRING_COMPARATOR));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToTreeSetUnmodifiable()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        SortedSet<String> dtos =
            MAPPER.transformToTreeSet(entities, STRING_COMPARATOR, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toSortedSet(STRING_COMPARATOR, "A", "C1", "C2")));
        assertThat(dtos.comparator(), is(STRING_COMPARATOR));

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
