package at.porscheinformatik.antimapper;

/**
 * A default implementation for a {@link Transformer}. It just handles the null-case.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
public abstract class AbstractTransformer<DTO, Entity> implements Transformer<DTO, Entity>
{

    private final Object[] defaultHints;

    public AbstractTransformer()
    {
        super();

        defaultHints = null;
    }

    public AbstractTransformer(Object... defaultHints)
    {
        super();

        this.defaultHints = defaultHints != null && defaultHints.length > 0 ? defaultHints : null;
    }

    @Override
    public Object[] getDefaultHints()
    {
        return defaultHints;
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
