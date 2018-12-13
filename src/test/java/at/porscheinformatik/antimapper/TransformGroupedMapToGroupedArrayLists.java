package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TransformGroupedMapToGroupedArrayLists extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedArrayLists()
    {
        Map<Character, List<String>> dtos =
            this.<Character> transformGrouped(null, BOARDING_PASS).toGroupedArrayLists();

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedArrayListsOrEmpty()
    {
        Map<Character, List<String>> dtos =
            this.<Character> transformGrouped(null, BOARDING_PASS, Hint.OR_EMPTY).toGroupedArrayLists();

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', TestUtils.toList("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toList("Z")));
    }

    @Test
    public void testToGroupedArrayLists()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, List<String>> dtos = this.transformGrouped(entities, BOARDING_PASS).toGroupedArrayLists();

        assertThat(dtos, matchesMap(
            toMap('A', is(toList("A1", "A1")), 'B', is(toList()), 'C', is(toList("C1", "C2")), null, nullValue())));

        // check modifiable
        dtos.put('Z', TestUtils.toList("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toList("Z")));
        dtos.get('A').add("Z");
        assertThat(dtos.get('A'), hasItem(is("Z")));
    }

    @Test
    public void testToGroupedArrayListsKeepNullAndUnmodifiable()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, List<String>> dtos =
            this.transformGrouped(entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS).toGroupedArrayLists();

        assertThat(dtos, matchesMap(toMap((Character) null, is(toList(new String[]{null})), 'A', is(toList("A1", "A1")),
            'B', is(toList(new String[]{null})), 'C', is(toList("C1", "C2")), null, nullValue())));

        try
        {
            dtos.put('Z', null);
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }

        try
        {
            dtos.get('A').add("A2");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
