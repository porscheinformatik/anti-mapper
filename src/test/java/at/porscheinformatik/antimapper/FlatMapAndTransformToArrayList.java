package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FlatMapAndTransformToArrayList extends AbstractMapperTest
{

    @Test
    public void testNullToArrayList()
    {
        List<String> dtos = MAPPER.flatMapAndTransformAll(null, BOARDING_PASS).toArrayList();

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToArrayListsOrEmpty()
    {
        List<String> dtos = MAPPER.flatMapAndTransformAll(null, BOARDING_PASS, Hint.OR_EMPTY).toArrayList();

        assertThat(dtos, is(Collections.emptyList()));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToArrayList()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        List<String> dtos = MAPPER.flatMapAndTransformAll(entities, BOARDING_PASS).toArrayList();

        assertThat(dtos, is(toList("A1", "A1", "C1", "C2")));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToArrayListsKeepNullAndUnmodifiable()
    {
        Map<Character, List<char[]>> entities = toMap('A', toList("A1".toCharArray(), "A1".toCharArray()), 'B',
            toList("!B".toCharArray()), 'C', toList("C1".toCharArray(), "C2".toCharArray()), null, null);
        List<String> dtos =
            MAPPER.flatMapAndTransformAll(entities, BOARDING_PASS, Hint.KEEP_NULL, Hint.UNMODIFIABLE).toArrayList();

        assertThat(dtos, is(toList("A1", "A1", null, "C1", "C2")));

        try
        {
            dtos.add('Z', null);
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
