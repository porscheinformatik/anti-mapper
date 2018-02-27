package at.porscheinformatik.antimapper;

/**
 * Function for transformer method.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface NonNullTransformerFunction<DTO, Entity>
{

    static <DTO, Entity> NonNullTransformerFunction<DTO, Entity> unsupported()
    {
        return (entity, hints) -> {
            throw new UnsupportedOperationException("Transform not supported");
        };
    }

    /**
     * Maps the entity to a DTO.
     *
     * @param entity the entity, never null
     * @param hints optional hints
     * @return the DTO
     */
    DTO transformNonNull(Entity entity, Object[] hints);

}