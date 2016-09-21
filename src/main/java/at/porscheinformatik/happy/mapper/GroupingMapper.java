package at.porscheinformatik.happy.mapper;

/**
 * A {@link GroupingTransformer} and {@link Merger} as one interface
 *
 * @author ham
 * @param <DTO_TYPE> the type of DTO
 * @param <GROUP_KEY_TYPE> the type of the group key
 * @param <ENTITY_TYPE> the type of entity
 */
public interface GroupingMapper<DTO_TYPE, GROUP_KEY_TYPE, ENTITY_TYPE>
    extends GroupingTransformer<DTO_TYPE, GROUP_KEY_TYPE, ENTITY_TYPE>, Merger<DTO_TYPE, ENTITY_TYPE>
{

    // intentionally left blank

}
