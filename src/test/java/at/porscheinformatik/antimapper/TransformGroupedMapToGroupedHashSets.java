package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class TransformGroupedMapToGroupedHashSets extends AbstractMapperTest
{

    @Test
    public void testNullToGroupedHashSets()
    {
        Map<Character, Set<String>> dtos = MAPPER.transformGroupedMapToGroupedHashSet(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToGroupedHashSetsOrEmpty()
    {
        Map<Character, Set<String>> dtos =
            MAPPER.transformGroupedMapToGroupedHashSet(null, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', TestUtils.toSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSet("Z")));
    }

    @Test
    public void testToGroupedHashSets()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, Set<String>> dtos = MAPPER.transformGroupedMapToGroupedHashSet(entities, BOARDING_PASS);

        assertThat(dtos, matchesMap(
            toMap('A', is(toSet("A1", "A1")), 'B', is(toSet()), 'C', is(toSet("C1", "C2")), null, nullValue())));

        // check modifiable
        dtos.put('Z', TestUtils.toSet("Z"));
        assertThat(dtos.get('Z'), is(TestUtils.toSet("Z")));
        dtos.get('A').add("Z");
        assertThat(dtos.get('A'), hasItem(is("Z")));
    }

    @Test
    public void testToGroupedHashSetsKeepNullAndUnmodifiable()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        Map<Character, Set<String>> dtos =
            MAPPER.transformGroupedMapToGroupedHashSet(entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, matchesMap(toMap((Character) null, is(toSet(new String[]{null})), 'A', is(toSet("A1", "A1")),
            'B', is(toSet(new String[]{null})), 'C', is(toSet("C1", "C2")), null, nullValue())));

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
