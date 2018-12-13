package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MergeGroupedMapIntoHashSetTest extends AbstractMapperTest
{

    @Test
    public void testNullGroupedMapIntoNullHashSet()
    {
        Map<Character, List<String>> dtos = null;
        Set<char[]> entities = null;
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullGroupedMapIntoNullHashSetOrEmpty()
    {
        Map<Character, List<String>> dtos = null;
        Set<char[]> entities = null;
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS, Hint.OR_EMPTY).intoHashSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoEmptyHashSet()
    {
        Map<Character, List<String>> dtos = null;
        Set<char[]> entities = TestUtils.toSet();
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoHashSet()
    {
        Map<Character, List<String>> dtos = null;
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("!a".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()),
                is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoNullHashSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        Set<char[]> entities = null;
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoNullHashSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        Set<char[]> entities = null;
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoEmptyHashSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        Set<char[]> entities = TestUtils.toSet();
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoHashSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("!a".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()),
                is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoEmptyHashSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        Set<char[]> entities = new HashSet<>();
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoHashSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        Set<char[]> entities = toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        Set<char[]> result = mergeGrouped(dtos, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("A".toCharArray()), is("A".toCharArray()), is("C2".toCharArray()),
                is("!a".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoHashSetKeepNullAndUnmodifiable()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        Set<char[]> entities = Collections
            .unmodifiableSet(toSet("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
                "c2".toCharArray(), null, "a".toCharArray()));
        Set<char[]> result = mergeGrouped(dtos, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).intoHashSet(entities);

        assertThat(describeResult(result), result,
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
