package at.porscheinformatik.antimapper;

/**
 * A {@link Transformer} and {@link Merger} as one interface
 *
 * @author ham
 * @param <DTO> the type of DTO
 * @param <Entity> the type of entity
 */
public interface Mapper<DTO, Entity> extends Transformer<DTO, Entity>, Merger<DTO, Entity>
{

    // intentionally left blank

}
