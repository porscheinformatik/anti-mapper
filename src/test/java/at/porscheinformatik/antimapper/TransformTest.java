package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

public class TransformTest extends AbstractMapperTest
{

    @Test
    public void testNull()
    {
        assertThat(this.transform(Optional.empty(), BOARDING_PASS), nullValue());
    }

    @Test
    public void test()
    {
        char[] entity = "A".toCharArray();

        assertThat(this.transform(entity, BOARDING_PASS), is("A"));
    }

    @Test
    public void testDeleted()
    {
        char[] entity = "!A".toCharArray();

        assertThat(this.transform(entity, BOARDING_PASS), nullValue());
    }

    @Test
    public void testEach()
    {
        List<char[]> entities = toList("A".toCharArray(), "A".toCharArray(), "!B".toCharArray(), "C1".toCharArray(),
            "C2".toCharArray(), null);
        List<String> dtos = transformAll(entities.stream(), BOARDING_PASS).toStream().collect(Collectors.toList());

        assertThat(dtos, is(toList("A", "A", "C1", "C2")));
    }

}
