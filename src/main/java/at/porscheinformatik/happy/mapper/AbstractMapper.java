package at.porscheinformatik.happy.mapper;

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
