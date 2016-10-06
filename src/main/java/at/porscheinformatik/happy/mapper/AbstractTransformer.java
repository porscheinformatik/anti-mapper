package at.porscheinformatik.happy.mapper;

/**
 * A default implementation for a {@link Transformer}. It just handles the null-case.
 *
 * @author ham
 * @param <DTO_TYPE> the type of the DTO
 * @param <ENTITY_TYPE> the type of the entity
 */
public abstract class AbstractTransformer<DTO_TYPE, ENTITY_TYPE> implements Transformer<DTO_TYPE, ENTITY_TYPE>
{

    @Override
    public final DTO_TYPE transform(ENTITY_TYPE entity, Object... hints)
    {
        if (entity == null)
        {
            return null;
        }

        return transformInternal(entity, hints);
    }

    /**
     * Maps the entity to a DTO.
     *
     * @param entity the entity, never null
     * @param hints optional hints
     * @return the DTO
     */
    protected abstract DTO_TYPE transformInternal(ENTITY_TYPE entity, Object[] hints);

}
