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
        Map<Character, SortedSet<String>> dtos = this.transformToGroupedTreeSets(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedTreeSetsOrEmpty()
    {
        Map<Character, SortedSet<String>> dtos =
            this.transformToGroupedTreeSets(null, GROUPER, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', TestUtils.toSortedSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSortedSet("Z")));
    }

    @Test
    public void testToGroupedTreeSets()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, SortedSet<String>> dtos =
            this.transformToGroupedTreeSets(entities, GROUPER, STRING_COMPARATOR, BOARDING_PASS);

        assertThat(dtos, matchesMap(
            toMap('A', is(toSortedSet(STRING_COMPARATOR, "A1")), 'C', is(toSortedSet(STRING_COMPARATOR, "C1", "C2")))));
        assertThat(dtos.get('A').comparator(), is(STRING_COMPARATOR));

        // check modifiable
        dtos.put('Z', TestUtils.toSortedSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSortedSet("Z")));
        dtos.get('A').add("Z");
        assertThat(dtos.get('A'), hasItem(is("Z")));
    }

    @Test
    public void testToGroupedTreeSetsKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, SortedSet<String>> dtos = this.transformToGroupedTreeSets(entities, GROUPER, STRING_COMPARATOR,
            Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos,
            matchesMap(toMap((Character) null, is(toSortedSet(STRING_COMPARATOR, (String) null)), 'A',
                is(toSortedSet(STRING_COMPARATOR, "A1", "A1")), 'B', is(toSortedSet(STRING_COMPARATOR, (String) null)),
                'C', is(toSortedSet(STRING_COMPARATOR, "C1", "C2")))));
        assertThat(dtos.get('A').comparator(), is(STRING_COMPARATOR));

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
