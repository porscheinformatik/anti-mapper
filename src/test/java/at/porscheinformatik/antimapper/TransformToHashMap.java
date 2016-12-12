package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TransformToHashMap extends AbstractMapperTest
{

    @Test
    public void testNullToHashMap()
    {
        Map<Character, String> dtos = MAPPER.transformToHashMap(null, GROUPER, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToHashMapOrEmpty()
    {
        Map<Character, String> dtos = MAPPER.transformToHashMap(null, GROUPER, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyMap()));

        // check modifiable
        dtos.put('Z', "Z");
        assertThat(dtos.get('Z'), is("Z"));
    }

    @Test
    public void testToHashMap()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, String> dtos = MAPPER.transformToHashMap(entities, GROUPER, BOARDING_PASS);

        assertThat(dtos, is(toMap('A', "A1", 'C', "C2")));

        // check modifiable
        dtos.put('Z', "Z");
        assertThat(dtos.get('Z'), is("Z"));
    }

    @Test
    public void testToHashMapKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A1".toCharArray(), "A1".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        Map<Character, String> dtos =
            MAPPER.transformToHashMap(entities, GROUPER, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toMap('A', "A1", 'B', null, 'C', "C2")));

        try
        {
            dtos.put('Z', "Z");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
