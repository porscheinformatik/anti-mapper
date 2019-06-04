package at.porscheinformatik.antimapper;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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
        if (left == null || left.length == 0)
        {
            return right;
        }

        if (right == null || right.length == 0)
        {
            return left;
        }

        Object[] result = Arrays.copyOf(left, left.length + right.length);

        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    /**
     * Returns the hint of the specified type. Throws an exception, if there's no value of the specified type.
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @return the hint
     * @throws IllegalArgumentException if no hint for the given type is available
     */
    public static <Any> Any hint(Object[] hints, Class<Any> type) throws IllegalArgumentException
    {
        return hint(hints, type, null);
    }

    /**
     * Returns the hint of the specified type. Uses the defaultHintSupplier (if available) if the value is not
     * specified. Throws an exception otherwise.
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @param defaultValueSupplier a {@link Supplier} of the default value is not specified
     * @return the hint, never null
     * @throws IllegalArgumentException if the hint could not be found
     */
    public static <Any> Any hint(Object[] hints, Class<Any> type, Supplier<Any> defaultValueSupplier)
        throws IllegalArgumentException
    {
        Any hint = hintOrNull(hints, type);

        if (hint != null)
        {
            return hint;
        }

        if (defaultValueSupplier != null)
        {
            return defaultValueSupplier.get();
        }

        throw new IllegalArgumentException(String
            .format("The hint of type %s is missing. Available hints are: %s", MapperUtils.toClassName(type),
                Arrays.stream(hints).map(MapperUtils::toClassName).collect(Collectors.joining(", "))));
    }

    /**
     * Returns the hint of the specified type.
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @param defaultValue a default value
     * @return the hint
     */
    public static <Any> Any hintOrElse(Object[] hints, Class<Any> type, Any defaultValue)
    {
        Any value = hintOrNull(hints, type);

        return value != null ? value : defaultValue;
    }

    /**
     * Returns the hint of the specified type, null if not available
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @return the hint
     */
    @SuppressWarnings("unchecked")
    public static <Any> Any hintOrNull(Object[] hints, Class<Any> type)
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
     * Returns the hint of the specified type.
     *
     * @param <Any> the type of the hint
     * @param hints the hints
     * @param type the type
     * @return an Optional with the hint, never null
     */
    public static <Any> Optional<Any> optionalHint(Object[] hints, Class<Any> type)
    {
        return Optional.ofNullable(hintOrNull(hints, type));
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
