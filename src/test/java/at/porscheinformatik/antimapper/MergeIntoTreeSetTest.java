package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class MergeIntoTreeSetTest extends AbstractMapperTest
{

    @Test
    public void testNullIntoNullTreeSet()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, nullValue());
    }

    @Test
    public void testNullIntoEmptyTreeSet()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = Collections.emptySortedSet();
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testNullIntoTreeSet()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testIntoNullTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(((TreeSet<char[]>) result).comparator(), is(CHAR_ARRAY_COMPARATOR));
    }

    @Test
    public void testEmptyIntoNullTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(((TreeSet<char[]>) result).comparator(), is(CHAR_ARRAY_COMPARATOR));
    }

    @Test
    public void testEmptyIntoEmptyTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testEmptyIntoTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testIntoEmptyTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testIntoTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = MAPPER.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testIntoTreeSetKeepNullAndUnmodifiable()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities =
            Collections.unmodifiableSortedSet(toTreeSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
                "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, "a".toCharArray()));

        SortedSet<char[]> result =
            MAPPER.mergeIntoTreeSet(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

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
