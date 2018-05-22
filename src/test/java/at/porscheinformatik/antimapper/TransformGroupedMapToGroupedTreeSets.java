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

public class TransformGroupedMapToGroupedTreeSets extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedTreeSets()
    {
        Map<Character, SortedSet<String>> dtos = MAPPER.transformGroupedMapToGroupedTreeSets(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedTreeSetsOrEmpty()
    {
        Map<Character, SortedSet<String>> dtos =
            MAPPER.transformGroupedMapToGroupedTreeSets(null, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', TestUtils.toSortedSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSortedSet("Z")));
    }

    @Test
    public void testToGroupedTreeSets()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, SortedSet<String>> dtos = MAPPER.transformGroupedMapToGroupedTreeSets(entities, BOARDING_PASS);

        assertThat(dtos,
            matchesMap(
                toMap('A', is(toSortedSet(STRING_COMPARATOR, "A1", "A1")), 'B', is(toSortedSet(STRING_COMPARATOR)), 'C',
                    is(toSortedSet(STRING_COMPARATOR, "C1", "C2")), null, nullValue())));

        // check modifiable
        dtos.put('Z', TestUtils.toSortedSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSortedSet("Z")));
        dtos.get('A').add("Z");
        assertThat(dtos.get('A'), hasItem(is("Z")));
    }

    @Test
    public void testToGroupedTreeSetsKeepNullAndUnmodifiable()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, SortedSet<String>> dtos = MAPPER.transformGroupedMapToGroupedTreeSets(entities,
            STRING_COMPARATOR, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap((Character) null, is(toSortedSet(STRING_COMPARATOR, new String[]{null})), 'A',
            is(toSortedSet(STRING_COMPARATOR, "A1", "A1")), 'B', is(toSortedSet(STRING_COMPARATOR, new String[]{null})),
            'C', is(toSortedSet(STRING_COMPARATOR, "C1", "C2")), null, nullValue())));

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
