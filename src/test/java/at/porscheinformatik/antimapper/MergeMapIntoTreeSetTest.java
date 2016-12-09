package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class MergeMapIntoTreeSetTest extends AbstractMapperTest
{

    @Test
    public void testNullMapIntoNullTreeSet()
    {
        Map<Integer, String> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, nullValue());
    }

    @Test
    public void testNullMapIntoNullTreeSetOrEmpty()
    {
        Map<Integer, String> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(result, is(Collections.emptySortedSet()));
    }

    @Test
    public void testNullMapIntoEmptyTreeSet()
    {
        Map<Integer, String> dtos = null;
        SortedSet<char[]> entities = Collections.emptySortedSet();
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testNullMapIntoTreeSet()
    {
        Map<Integer, String> dtos = null;
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoNullTreeSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(((TreeSet<char[]>) result).comparator(), is(CHAR_ARRAY_COMPARATOR));
    }

    @Test
    public void testEmptyMapIntoNullTreeSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(((TreeSet<char[]>) result).comparator(), is(CHAR_ARRAY_COMPARATOR));
    }

    @Test
    public void testEmptyMapIntoEmptyTreeSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testEmptyMapIntoTreeSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoEmptyTreeSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoTreeSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeMapIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoTreeSetKeepNullAndUnmodifiable()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        SortedSet<char[]> entities =
            Collections.unmodifiableSortedSet(toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
                "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, "a".toCharArray()));

        SortedSet<char[]> result =
            MAPPER.mergeMapIntoTreeSet(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(result, matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()),
            is("!b".toCharArray()), is("C1".toCharArray()), nullValue())));

        try
        {
            result.add("Z".toCharArray());
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
