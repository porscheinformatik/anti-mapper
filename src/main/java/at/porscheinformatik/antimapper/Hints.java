package at.porscheinformatik.antimapper;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utilities for hints
 *
 * @author ham
 */
public final class Hints
{

    private Hints()
    {
        super();
    }

    /**
     * Joins the left and right array
     *
     * @param left the left array
     * @param right the right array
     * @return the merge array
     */
    public static Object[] join(Object[] left, Object... right)
    {
        if ((left == null) || (left.length == 0))
        {
            return right;
        }

        Object[] result = Arrays.copyOf(left, left.length + right.length);

        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    /**
     * Returns the hint of the specified type. Null if not found
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @return the hint
     * @throws IllegalArgumentException if no hint for the given type is available
     */
    public static <Any> Any hint(Object[] hints, Class<Any> type)
    {
        return hint(hints, type, null);
    }

    /**
     * Returns the hint of the specified type. Null if not found
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @param defaultHintSupplier a {@link Supplier} of the default value if not specified
     * @return the hint
     */
    public static <Any> Any hint(Object[] hints, Class<Any> type, Supplier<Any> defaultHintSupplier)
    {
        Any hint = optionalHint(hints, type);

        if (hint != null)
        {
            return hint;
        }

        if (defaultHintSupplier != null)
        {
            return defaultHintSupplier.get();
        }

        throw new IllegalArgumentException(
            String.format("The hint of type %s is missing. Available hints are: %s", MapperUtils.toClassName(type),
                Arrays.stream(hints).map(MapperUtils::toClassName).collect(Collectors.joining(", "))));
    }

    /**
     * Returns the hint of the specified type. Null if not found
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @return the hint
     */
    @SuppressWarnings("unchecked")
    public static <Any> Any optionalHint(Object[] hints, Class<Any> type)
    {
        if (hints != null)
        {
            for (int i = hints.length - 1; i >= 0; i--)
            {
                if (type.isInstance(hints[i]))
                {
                    return (Any) hints[i];
                }
            }
        }

        return null;
    }

    /**
     * Returns true if the hints contain at least one value of the specified type.
     *
     * @param hints the hints
     * @param type the type
     * @return true if available
     */
    public static boolean containsHint(Object[] hints, Class<?> type)
    {
        if (hints != null)
        {
            for (Object hint : hints)
            {
                if (type.isInstance(hint))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the hints contain at least one value that equals the specified object
     *
     * @param hints the hints
     * @param object an object
     * @return true if available
     */
    public static boolean containsHint(Object[] hints, Object object)
    {
        if (hints != null)
        {
            for (Object hint : hints)
            {
                if (Objects.equals(object, hint))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a string describing the classes of the specified hints
     *
     * @param hints the hints
     * @return the string
     */
    public static String toClassString(Object[] hints)
    {
        if (hints == null)
        {
            return null;
        }

        return "Hints["
            + Arrays.stream(hints).map(hint -> MapperUtils.toClassName(hint)).collect(Collectors.joining(", "))
            + "]";
    }

}
