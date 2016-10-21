package at.porscheinformatik.antimapper;

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
            return transformNull(hints);
        }

        return transformNonNull(entity, hints);
    }

    /**
     * Maps null to a dto. The default implementation returns null.
     *
     * @param hints optional hints
     * @return the DTO
     */
    protected DTO_TYPE transformNull(Object[] hints)
    {
        return null;
    }

    /**
     * Maps the entity to a DTO.
     *
     * @param entity the entity, never null
     * @param hints optional hints
     * @return the DTO
     */
    protected abstract DTO_TYPE transformNonNull(ENTITY_TYPE entity, Object[] hints);

}
