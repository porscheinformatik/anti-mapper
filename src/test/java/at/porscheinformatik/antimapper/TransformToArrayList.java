package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TransformToArrayList extends AbstractMapperTest
{

    @Test
    public void testNullToArrayList()
    {
        List<String> dtos = transformToArrayList(null, BOARDING_PASS);

        assertThat(dtos, nullValue());
    }

    @Test
    public void testNullToArrayListOrEmpty()
    {
        List<String> dtos = transformToArrayList(null, BOARDING_PASS, Hint.OR_EMPTY);

        assertThat(dtos, is(Collections.emptyList()));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToArrayList()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        List<String> dtos = transformToArrayList(entities, BOARDING_PASS);

        assertThat(dtos, is(toList("A", "A", "C1", "C2")));

        // check modifiable
        dtos.add("Z");
        assertThat(dtos, hasItem(is("Z")));
    }

    @Test
    public void testToArrayListKeepNullAndUnmodifiable()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        List<String> dtos = transformToArrayList(entities, Hint.KEEP_NULL, Hint.UNMODIFIABLE, BOARDING_PASS);

        assertThat(dtos, is(toList("A", "A", null, "C1", "C2", null)));

        try
        {
            dtos.add("Z");
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }
    }

}
