package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class MergeIntoArrayListTest extends AbstractMapperTest
{

    @Test
    public void testNullIntoNullArrayList()
    {
        Collection<String> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullIntoNullArrayListOrEmpty()
    {
        Collection<String> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS, Hint.OR_EMPTY).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullIntoEmptyArrayList()
    {
        Collection<String> dtos = null;
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullIntoArrayList()
    {
        Collection<String> dtos = null;
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
    public void testIntoNullArrayList()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, matchesList(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoNullArrayList()
    {
        Collection<String> dtos = Collections.emptyList();
        List<char[]> entities = null;
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoEmptyArrayList()
    {
        Collection<String> dtos = Collections.emptyList();
        List<char[]> entities = TestUtils.toList();
        List<char[]> result = mergeAll(dtos, BOARDING_PASS).intoArrayList(entities);

        assertThat(describeResult(result), result, is(Collections.emptyList()));
        assertThat(describeResult(result), result, sameInstance(entities));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoArrayList()
    {
        Collection<String> dtos = Collections.emptyList();
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
    public void testIntoEmptyArrayList()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
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
    public void testIntoArrayList()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
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
    public void testIntoArrayListKeepNullAndUnmodifiable()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
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

}
