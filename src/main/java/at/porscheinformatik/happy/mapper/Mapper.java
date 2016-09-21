package at.porscheinformatik.happy.mapper;

/**
 * A {@link Transformer} and {@link Merger} as one interface
 *
 * @author ham
 * @param <DTO_TYPE> the type of DTO
 * @param <ENTITY_TYPE> the type of entity
 */
public interface Mapper<DTO_TYPE, ENTITY_TYPE> extends Transformer<DTO_TYPE, ENTITY_TYPE>, Merger<DTO_TYPE, ENTITY_TYPE>
{

    // intentionally left blank

}
