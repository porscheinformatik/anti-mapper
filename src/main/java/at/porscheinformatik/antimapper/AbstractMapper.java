package at.porscheinformatik.antimapper;

/**
 * An implementation of a {@link Transformer} and extends the {@link AbstractMerger}.
 *
 * @author ham
 * @param <DTO> the type of DTO
 * @param <Entity> the type of entity
 */
public abstract class AbstractMapper<DTO, Entity> extends AbstractMerger<DTO, Entity> implements Mapper<DTO, Entity>
{

    public AbstractMapper()
    {
        super();
    }

    public AbstractMapper(Object... defaultHints)
    {
        super(defaultHints);
    }

    @Override
    public final DTO transform(Entity entity, Object... hints)
    {
        hints = Hints.join(defaultHints, hints);

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
    protected DTO transformNull(Object[] hints)
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
    protected abstract DTO transformNonNull(Entity entity, Object[] hints);

}
