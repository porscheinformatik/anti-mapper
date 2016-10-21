package at.porscheinformatik.antimapper;

/**
 * A referer is a mapper that creates a dummy entity for a DTO, that just contains the id.
 *
 * @author ham
 * @param <DTO_TYPE> the dto type
 * @param <ENTITY_TYPE> the entity type
 */
public interface Referer<DTO_TYPE, ENTITY_TYPE>
{

    /**
     * Creates an entity as reference. If the DTO is null the method returns null. If the id of the entity matches the
     * id of the dto, it returns the entity. If the ids do not match, it creates a new empty entity, that contains just
     * the id.
     *
     * @param dto the dto, may be null
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the entity, either the passed one, or a newly created one
     */
    default ENTITY_TYPE refer(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints)
    {
        throw new UnsupportedOperationException("Refer not supported by " + MapperUtils.toClassName(getClass()));
    }

}
