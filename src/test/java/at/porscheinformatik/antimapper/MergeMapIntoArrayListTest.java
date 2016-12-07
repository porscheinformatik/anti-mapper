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

public class MergeMapIntoArrayListTest extends AbstractMapperTest
{

    @Test
    public void testNullMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = null;
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, nullValue());
    }

    @Test
    public void testNullMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = Collections.emptyList();
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptyList()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testNullMapIntoArrayList()
    {
        Map<Integer, String> dtos = null;
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()),
            is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = null;
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesList(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
    }

    @Test
    public void testEmptyMapIntoNullArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = null;
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptyList()));
    }

    @Test
    public void testEmptyMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = Collections.emptyList();
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, is(Collections.emptyList()));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testEmptyMapIntoArrayList()
    {
        Map<Integer, String> dtos = Collections.emptyMap();
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesList(toList(is("!a".toCharArray()), is("!a".toCharArray()), is("!b".toCharArray()),
            is("!c1".toCharArray()), is("!c2".toCharArray()), is("!a".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoEmptyArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = new ArrayList<>();
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesList(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoArrayList()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result = MAPPER.mergeMapIntoArrayList(dtos, entities, BOARDING_PASS);

        assertThat(result, matchesList(toList(is("A".toCharArray()), is("C2".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("C1".toCharArray()), is("A".toCharArray()))));
        assertThat(result, sameInstance(entities));
    }

    @Test
    public void testMapIntoArrayListKeepNullAndUnmodifiable()
    {
        Map<Integer, String> dtos = toMap(1, "A", 2, "C2", 3, "C1", 4, null, 5, "A");
        List<char[]> entities = toList("a".toCharArray(), "a".toCharArray(), "!b".toCharArray(), "c1".toCharArray(),
            "c2".toCharArray(), null, null, "a".toCharArray());
        List<char[]> result =
            MAPPER.mergeMapIntoArrayList(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(result, matchesList(toList(is("A".toCharArray()), is("C2".toCharArray()), is("!a".toCharArray()),
            is("!b".toCharArray()), is("C1".toCharArray()), nullValue(), nullValue(), is("A".toCharArray()))));

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
