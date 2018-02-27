package at.porscheinformatik.antimapper;

/**
 * Function for merger method.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface NonNullMergerFunction<DTO, Entity>
{

    static <DTO, Entity> NonNullMergerFunction<DTO, Entity> unsupported()
    {
        return (dto, entity, hints) -> {
            throw new UnsupportedOperationException("Merge not supported");
        };
    }

    /**
     * Maps the DTO to an entity. Neither the DTO nor the entity is null.
     *
     * @param dto the dto
     * @param entity the entity
     * @param hints optional hints
     * @return the entity, either the passed one, or a newly created one
     */
    Entity mergeNonNull(DTO dto, Entity entity, Object[] hints);

}