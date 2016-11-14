package at.porscheinformatik.antimapper;

/**
 * Provides a matching operation
 *
 * @param <DTO> the type of the left object
 * @param <Entity> the type of the right object
 * @author ham
 */
@FunctionalInterface
public interface MatchFunction<DTO, Entity>
{

    /**
     * Returns true if the left object matches the right object. The function decides how the objects should be
     * compared.
     *
     * @param left the left object
     * @param right the right object
     * @return true if the left object matches the right object, false otherwise
     */
    boolean matches(DTO left, Entity right);

}
