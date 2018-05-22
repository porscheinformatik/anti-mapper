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
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, nullValue());
    }

    @Test
    public void testNullIntoNullTreeSetOrEmpty()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result =
            this.mergeIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(describeResult(result), result, is(Collections.emptySortedSet()));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullIntoEmptyTreeSet()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = TestUtils.toSortedSet(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testNullIntoTreeSet()
    {
        Collection<String> dtos = null;
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testIntoNullTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoNullTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = null;
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, CHAR_ARRAY_COMPARATOR, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoEmptyTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, is(Collections.emptySet()));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testEmptyIntoTreeSet()
    {
        Collection<String> dtos = Collections.emptyList();
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("!a".toCharArray()), is("!b".toCharArray()), is("!c1".toCharArray()), is("!c2".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testIntoEmptyTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = new TreeSet<>(CHAR_ARRAY_COMPARATOR);
        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result,
            matchesCollection(toList(is("A".toCharArray()), is("C2".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testIntoTreeSet()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities = toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
            "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, null, "a".toCharArray());

        SortedSet<char[]> result = this.mergeIntoTreeSet(dtos, entities, BOARDING_PASS);

        assertThat(describeResult(result), result, matchesCollection(
            toList(is("A".toCharArray()), is("C2".toCharArray()), is("!b".toCharArray()), is("C1".toCharArray()))));
        assertThat(describeResult(result), result, sameInstance(entities));
        assertThat(describeResult(result), result.comparator(), is(CHAR_ARRAY_COMPARATOR));

        // check modifiable
        result.add("Z".toCharArray());
        assertThat(describeResult(result), result, hasItem(is("Z".toCharArray())));
    }

    @Test
    public void testIntoTreeSetKeepNullAndUnmodifiable()
    {
        Collection<String> dtos = toList("A", "C2", "C1", null, "A");
        SortedSet<char[]> entities =
            Collections.unmodifiableSortedSet(toSortedSet(CHAR_ARRAY_COMPARATOR, "a".toCharArray(), "a".toCharArray(),
                "!b".toCharArray(), "c1".toCharArray(), "c2".toCharArray(), null, "a".toCharArray()));

        SortedSet<char[]> result =
            this.mergeIntoTreeSet(dtos, entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

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

}
