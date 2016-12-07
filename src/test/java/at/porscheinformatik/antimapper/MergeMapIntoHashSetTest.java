package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MergeMapIntoHashSetTest extends AbstractMapperTest
{

    @Test
    public void testNullMapIntoNullHashSet()
    {
        Map<Integer, String> dtos = null;
        Set<char[]> entities = null;
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, nullValue());
    }

    @Test
    public void testNullMapIntoEmptyHashSet()
    {
        Map<Integer, String> dtos = null;
        Set<char[]> entities = Collections.emptySet();
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testNullMapIntoHashSet()
    {
        Map<Integer, String> dtos = null;
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoNullHashSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        Set<char[]> entities = null;
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
    }

    @Test
    public void testEmptyMapIntoNullHashSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        Set<char[]> entities = null;
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
    }

    @Test
    public void testEmptyMapIntoEmptyHashSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        Set<char[]> entities = Collections.emptySet();
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptySet()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testEmptyMapIntoHashSet()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoEmptyHashSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        Set<char[]> entities = new HashSet<>();
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoHashSet()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = MAPPER.mergeMapIntoHashSet(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesCollection(toList(is("A".toCharArray()), is("A".toCharArray()),
            is("C2".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoHashSetKeepNullAndUnmodifiable()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        Set<char[]> entities = Collections.unmodifiableSet(toSet("a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, "a".toCharArray()));
        Set<char[]> result =
            MAPPER.mergeMapIntoHashSet(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("!a".toCharArray()),
                is("!b".toCharArray()), is("C1".toCharArray()), nullValue(), is("A".toCharArray()))));

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
