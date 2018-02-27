package at.porscheinformatik.antimapper;

/**
 * Creates a new entity
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface CreateEntityFunction<DTO, Entity>
{

    static <DTO, Entity> CreateEntityFunction<DTO, Entity> unsupported()
    {
        return (entity, hints) -> {
            throw new UnsupportedOperationException("CreateEntity not supported");
        };
    }

    /**
     * Create a new entity.
     *
     * @param dto the dto the DTO
     * @param hints the hints the hints
     * @return the entity, never null
     */
    Entity create(DTO dto, Object[] hints);

}