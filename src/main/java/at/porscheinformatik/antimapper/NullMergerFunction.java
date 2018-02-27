package at.porscheinformatik.antimapper;

/**
 * Function for merger method.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface NullMergerFunction<DTO, Entity>
{

    static <DTO, Entity> NullMergerFunction<DTO, Entity> alwaysToNull()
    {
        return (entity, hints) -> null;
    }

    /**
     * Maps null to an entity.
     *
     * @param entity the entity, never null
     * @param hints optional hints
     * @return the entity, either the passed one, or a newly created one
     */
    Entity mergeNull(Entity entity, Object[] hints);

}