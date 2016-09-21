package at.porscheinformatik.happy.mapper;

/**
 * Provides a matching operation
 *
 * @param <LEFT_TYPE> the type of the left object
 * @param <RIGHT_TYPE> the type of the right object
 * @author ham
 */
@FunctionalInterface
public interface MatchFunction<LEFT_TYPE, RIGHT_TYPE>
{

    /**
     * Returns true if the left object matches the right object. The function decides how the objects should be
     * compared.
     *
     * @param left the left object
     * @param right the right object
     * @return true if the left object matches the right object, false otherwise
     */
    boolean matches(LEFT_TYPE left, RIGHT_TYPE right);

}
