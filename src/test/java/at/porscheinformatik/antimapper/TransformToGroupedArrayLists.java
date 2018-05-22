package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TransformToGroupedArrayLists extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedArrayLists()
    {
        Map<Character, List<String>> dtos = this.transformToGroupedArrayLists(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedArrayListsOrEmpty()
    {
        Map<Character, List<String>> dtos =
            this.transformToGroupedArrayLists(null, GROUPER, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', TestUtils.toList("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toList("Z")));
    }

    @Test
    public void testToGroupedArrayLists()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, List<String>> dtos = this.transformToGroupedArrayLists(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap('A', is(toList("A1", "A1")), 'C', is(toList("C1", "C2")))));

        // check modifiable
        dtos.put('Z', TestUtils.toList("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toList("Z")));
        dtos.get('A').add("Z");
        assertThat(dtos.get('A'), hasItem(is("Z")));
    }

    @Test
    public void testToGroupedArrayListsKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, List<String>> dtos =
            this.transformToGroupedArrayLists(entities, GROUPER, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap((Character) null, is(toList(new String[]{null})), 'A', is(toList("A1", "A1")),
            'B', is(toList(new String[]{null})), 'C', is(toList("C1", "C2")))));

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
