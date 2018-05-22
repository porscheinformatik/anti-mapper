package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MergeGroupedMapIntoArrayListTest extends AbstractMapperTest
{

    @Test
    public void testNullGroupedMapIntoNullArrayList()
    {
        Map<Character, List<String>> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullGroupedMapIntoNullArrayListOrEmpty()
    {
        Map<Character, List<String>> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoEmptyArrayList()
    {
        Map<Character, List<String>> dtos = null;
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoArrayList()
    {
        Map<Character, List<String>> dtos = null;
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoNullArrayList()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        List<char[]> entities = null;
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesList(
            toList(is("A".toCharArray()), is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoNullArrayList()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        List<char[]> entities = null;
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoEmptyArrayList()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoArrayList()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoEmptyArrayList()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        List<char[]> entities = new ArrayList<>();
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesList(
            toList(is("A".toCharArray()), is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoArrayList()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeGroupedMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesList(toList(is("A".toCharArray()), is("A".toCharArray()),
            is("C2".toCharArray()), is("C1".toCharArray()), is("!b".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoArrayListKeepNullAndUnmodifiable()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result =
            mergeGroupedMapIntoArrayList(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(describeResult(result), result,
            matchesList(toList(is("A".toCharArray()), is("A".toCharArray()), is("C2".toCharArray()),
                is("C1".toCharArray()), nullValue(), is("!b".toCharArray()), nullValue(), is("!a".toCharArray()))));

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
