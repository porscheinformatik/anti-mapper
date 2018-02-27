package at.porscheinformatik.antimapper;

/**
 * Function for transformer method.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface NullTransformerFunction<DTO, Entity>
{

    static <DTO, Entity> NullTransformerFunction<DTO, Entity> alwaysToNull()
    {
        return hints -> null;
    }

    /**
     * Maps null to a DTO.
     *
     * @param hints optional hints
     * @return the DTO
     */
    DTO transformNull(Object[] hints);

}