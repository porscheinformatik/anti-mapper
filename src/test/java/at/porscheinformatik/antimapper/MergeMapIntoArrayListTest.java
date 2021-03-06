package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class MergeMapIntoArrayListTest extends AbstractMapperTest
{

    @Test
    public void testNullMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullMapIntoNullArrayListOrEmpty()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS, Hint.OR_EMPTY).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullMapIntoArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyMapIntoArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = new ArrayList<>();
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testMapIntoArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(toList(is("A".toCharArray()), is("C2".toCharArray()),
            is("C1".toCharArray()), is("A".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testMapIntoArrayListKeepNullAndUnmodifiable()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = mergeAll(dtos, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result,
            matchesList(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), nullValue(),
                is("A".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()), nullValue())));

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

    @Override
    public boolean isUniqueKeyMatching(String dto, char[] entity, Object... hints)
    {
        // make sure, there is a key
        Assert.assertTrue(Hints.containsHint(hints, Integer.class));

        return super.isUniqueKeyMatching(dto, entity, hints);
    }

}
