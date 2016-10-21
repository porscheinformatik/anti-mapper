package at.porscheinformatik.antimapper;

/**
 * An implementation of a {@link Transformer} and extends the {@link AbstractMerger}.
 *
 * @author ham
 * @param <DTO_TYPE> the type of DTO
 * @param <ENTITY_TYPE> the type of entity
 */
public abstract class AbstractMapper<DTO_TYPE, ENTITY_TYPE> extends AbstractMerger<DTO_TYPE, ENTITY_TYPE>
    implements Mapper<DTO_TYPE, ENTITY_TYPE>
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
