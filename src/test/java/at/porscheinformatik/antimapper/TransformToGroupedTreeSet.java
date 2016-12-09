package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

public class TransformToGroupedTreeSet extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedTreeSets()
    {
        Map<Character, SortedSet<String>> dtos = MAPPER.transformToGroupedTreeSets(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedTreeSetsOrEmpty()
    {
        Map<Character, SortedSet<String>> dtos =
            MAPPER.transformToGroupedTreeSets(null, GROUPER, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));
    }

    @Test
    public void testToGroupedTreeSets()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, SortedSet<String>> dtos = MAPPER.transformToGroupedTreeSets(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, matchesMap(
            toMap('A', is(toTreeSet(STRING_COMPARATOR, "A1")), 'C', is(toTreeSet(STRING_COMPARATOR, "C1", "C2")))));
    }

    @Test
    public void testToGroupedTreeSetsKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, SortedSet<String>> dtos = MAPPER.transformToGroupedTreeSets(entities, GROUPER, STRING_COMPARATOR,
            Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos,
            matchesMap(toMap((Character) null, is(toTreeSet(STRING_COMPARATOR, (String) null)), 'A',
                is(toTreeSet(STRING_COMPARATOR, "A1", "A1")), 'B', is(toTreeSet(STRING_COMPARATOR, (String) null)), 'C',
                is(toTreeSet(STRING_COMPARATOR, "C1", "C2")))));

        try
        {
            dtos.put('Z', null);
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }

        try
        {
            dtos.get('A').add("A2");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
