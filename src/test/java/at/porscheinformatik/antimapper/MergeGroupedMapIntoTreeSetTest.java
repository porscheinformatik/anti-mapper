package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class MergeGroupedMapIntoTreeSetTest extends AbstractMapperTest
{

    @Test
    public void testNullGroupedMapIntoNullTreeSet()
    {
        Map<Character, List<String>> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullGroupedMapIntoNullTreeSetOrEmpty()
    {
        Map<Character, List<String>> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result =
            this.mergeGrouped(dtos, BOARDING_PASS, Hint.OR_EMPTY).intoTreeSet(entities, CHAR_ARRAY_COMPARATOR);

        assertThat(describeResult(result), result, is(Collections.emptySortedSet()));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoEmptyTreeSet()
    {
        Map<Character, List<String>> dtos = null;
        SortedSet<char[]> entities = TestUtils.toSortedSet(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullGroupedMapIntoTreeSet()
    {
        Map<Character, List<String>> dtos = null;
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoNullTreeSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities, CHAR_ARRAY_COMPARATOR);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoNullTreeSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities, CHAR_ARRAY_COMPARATOR);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoEmptyTreeSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyGroupedMapIntoTreeSet()
    {
        Map<Character, List<String>> dtos = Collections.emptyMap();
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoEmptyTreeSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoTreeSet()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeGrouped(dtos, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testGroupedMapIntoTreeSetKeepNullAndUnmodifiable()
    {
        Map<Character, List<String>> dtos =
            toMap('a', toList("A", "A"), 'c', toList("C2", "C1"), null, toList((String) null));
        SortedSet<char[]> entities = Collections
            .unmodifiableSortedSet(toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
                "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, "a".toCharArray()));

        SortedSet<char[]> result =
            this.mergeGrouped(dtos, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).intoTreeSet(entities);

        assertThat(describeResult(result), result, matchesCollection(toList(is("A".toCharArray()),
            is("C2".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()), nullValue())));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

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

    @Test
    public void testBug1()
    {
        Map<Character, List<String>> dtos = toMap('1', toList("A", "B"), '2', toList("C"));
        SortedSet<char[]> entities = Collections
            .unmodifiableSortedSet(toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "b".toCharArray(),
                "c".toCharArray(), "d".toCharArray()));

        SortedSet<char[]> result =
            this.mergeGrouped(dtos, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).intoTreeSet(entities);

        System.out.println(result);
        //
        //        assertThat(describe(result), result, matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()),
        //            is("!b".toCharArray()), is("C1".toCharArray()), nullValue())));
        //        assertThat(describe(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));
        //
        //        try
        //        {
        //            result.add("Z".toCharArray());
        //            fail();
        //        }
        //        catch (UnsupportedOperationException e)
        //        {
        //            // expected
        //        }
    }

}
