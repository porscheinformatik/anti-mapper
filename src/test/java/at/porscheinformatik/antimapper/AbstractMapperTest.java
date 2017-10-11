package at.porscheinformatik.antimapper;

import static at.porscheinformatik.antimapper.TestUtils.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;

public abstract class AbstractMapperTest
{

    protected static final UUID BOARDING_PASS = UUID.randomUUID();

    protected static final Comparator<char[]> CHAR_ARRAY_COMPARATOR = (left, right) -> {
        return compare(left != null ? String.valueOf(left) : null, right != null ? String.valueOf(right) : null);
    };

    protected static final Comparator<String> STRING_COMPARATOR = (left, right) -> {
        return compare(left, right);
    };

    protected static final Function<char[], Character> GROUPER = entity -> {
        if (entity == null || entity.length == 0)
        {
            return null;
        }

        if (entity[0] == '!')
        {
            return entity[1];
        }

        return entity[0];
    };

    /**
     * This cool mapper that maps Strings and char-arrays. The char-array is the entity, the String is the DTO. The
     * unique check ignores the case. If the entity starts with a "!", it's a deleted entity.
     */
    protected static final Mapper<String, char[]> MAPPER = new AbstractMapper<String, char[]>()
    {

        @Override
        protected String transformNull(Object[] hints)
        {
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            return null;
        };

        @Override
        protected String transformNonNull(char[] entity, Object[] hints)
        {
            Assert.assertNotNull(entity);
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            if (entity.length > 0 && entity[0] == '!')
            {
                return null;
            }

            return String.valueOf(entity);
        }

        @Override
        protected char[] mergeNull(char[] entity, Object[] hints)
        {
            Assert.assertNotNull(entity);
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            if (entity.length == 0)
            {
                return new char[]{'!'};
            }

            if (entity[0] == '!')
            {
                return entity;
            }

            char[] result = new char[entity.length + 1];

            result[0] = '!';
            System.arraycopy(entity, 0, result, 1, entity.length);

            return result;
        };

        @Override
        protected char[] mergeNonNull(String dto, char[] entity, Object[] hints)
        {
            Assert.assertNotNull(dto);
            Assert.assertNotNull(entity);
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            if (dto.length() == entity.length)
            {
                System.arraycopy(dto.toCharArray(), 0, entity, 0, entity.length);

                return entity;
            }

            return dto.toCharArray();
        }

        @Override
        protected char[] create(String dto, Object[] hints)
        {
            Assert.assertNotNull(dto);
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            return new char[dto.length()];
        }

        @Override
        public boolean isUniqueKeyMatching(String dto, char[] entity, Object... hints)
        {
            Assert.assertNotNull(dto);
            Assert.assertNotNull(entity);
            Assert.assertNotNull(hints);
            Assert.assertEquals("Hints were not passed correctly", BOARDING_PASS,
                Hints.optionalHint(hints, UUID.class));

            String entityString = String.valueOf(entity);

            if (entityString.startsWith("!"))
            {
                entityString = entityString.substring(1);
            }

            if (dto.startsWith("!"))
            {
                dto = entityString.substring(1);
            }

            return Objects.equals(dto.toLowerCase(), entityString.toLowerCase());
        }

    };

    public String describeResult(Collection<char[]> chars)
    {
        return "The result of the mapping is: "
            + (chars != null
                ? chars.stream().map($ -> $ != null ? new String($) : "null").collect(Collectors.joining(","))
                : "null");
    }

}
