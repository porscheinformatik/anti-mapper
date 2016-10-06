package at.porscheinformatik.happy.mapper;

/**
 * An abstract base implementation of a {@link Merger}. It just handles the null-case
 *
 * @author ham
 * @param <DTO_TYPE> the dto type
 * @param <ENTITY_TYPE> the entity type
 */
public abstract class AbstractMerger<DTO_TYPE, ENTITY_TYPE> implements Merger<DTO_TYPE, ENTITY_TYPE>
{

    @Override
    public ENTITY_TYPE merge(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints)
    {
        if (dto == null)
        {
            return null;
        }

        if ((entity == null) || (!isUniqueKeyMatching(dto, entity, hints)))
        {
            entity = create(dto, hints);
        }

        return mergeInternal(dto, entity, hints);
    }

    /**
     * Maps the DTO to an entity. Neither the DTO nor the entity is null.
     *
     * @param dto the dto
     * @param entity the entity
     * @param hints optional hints
     * @return the entity, either the passed one, or a newly created one
     */
    protected abstract ENTITY_TYPE mergeInternal(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);

    /**
     * Create a new entity. Set basic values, that will not be set by the
     * {@link #mergeInternal(Object, Object, Object...)} method, e.g. the ID.
     *
     * @param dto the dto the DTO
     * @param hints the hints the hints
     * @return the entity, never null
     */
    protected abstract ENTITY_TYPE create(DTO_TYPE dto, Object... hints);

}
