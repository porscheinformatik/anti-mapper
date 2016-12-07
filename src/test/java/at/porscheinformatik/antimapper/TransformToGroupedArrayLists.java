package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

public class TransformToGroupedArrayLists extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedArrayLists()
    {
        Map<Character, List<String>> dtos = MAPPER.transformToGroupedArrayLists(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testToGroupedArrayLists()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, List<String>> dtos = MAPPER.transformToGroupedArrayLists(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap('A', is(toList("A1", "A1")), 'C', is(toList("C1", "C2")))));
    }

    @Test
    public void testToGroupedArrayListsKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, List<String>> dtos =
            MAPPER.transformToGroupedArrayLists(entities, GROUPER, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap((Character) null, is(toList(new String[]{null})), 'A', is(toList("A1", "A1")),
            'B', is(toList(new String[]{null})), 'C', is(toList("C1", "C2")))));

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

    @Test
    public void testNullToGroupedHashSets()
    {
        Map<Character, Set<String>> dtos = MAPPER.transformToGroupedHashSets(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testToGroupedHashSets()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, Set<String>> dtos = MAPPER.transformToGroupedHashSets(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap('A', is(toSet("A1")), 'C', is(toSet("C1", "C2")))));
    }

    @Test
    public void testToGroupedHashSetsKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, Set<String>> dtos =
            MAPPER.transformToGroupedHashSets(entities, GROUPER, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap((Character) null, is(toSet(new String[]{null})), 'A', is(toSet("A1", "A1")),
            'B', is(toSet(new String[]{null})), 'C', is(toSet("C1", "C2")))));

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

    @Test
    public void testNullToTreeSet()
    {
        Set<String> dtos = MAPPER.transformToTreeSet(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
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

    @Test
    public void testNullToGroupedTreeSets()
    {
        Map<Character, SortedSet<String>> dtos = MAPPER.transformToGroupedTreeSets(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
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

    @Test
    public void testNullToHashMap()
    {
        Map<Character, String> dtos = MAPPER.transformToHashMap(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testToHashMap()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, String> dtos = MAPPER.transformToHashMap(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, is(toMap('A', "A1", 'C', "C2")));
    }

    @Test
    public void testToHashMapKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, String> dtos =
            MAPPER.transformToHashMap(entities, GROUPER, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toMap('A', "A1", 'B', null, 'C', "C2")));

        try
        {
            dtos.put('Z', "Z");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
