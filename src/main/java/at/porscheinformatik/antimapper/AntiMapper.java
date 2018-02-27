package at.porscheinformatik.antimapper;

public class AntiMapper<DTO, Entity> extends AbstractMapper<DTO, Entity>
{

    public static <DTO, Entity> AntiMapper<DTO, Entity> transformer(
        NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction)
    {
        return new AntiMapper<DTO, Entity>().withTransformer(nonNullTransformerFunction);
    }

    public static <DTO, Entity> AntiMapper<DTO, Entity> transformer(
        NullTransformerFunction<DTO, Entity> nullTransformerFunction,
        NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction)
    {
        return new AntiMapper<DTO, Entity>().withTransformer(nullTransformerFunction, nonNullTransformerFunction);
    }

    public static <DTO, Entity> AntiMapper<DTO, Entity> merger(NonNullMergerFunction<DTO, Entity> nonNullMergerFunction,
        CreateEntityFunction<DTO, Entity> createEntityFunction,
        UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction)
    {
        return new AntiMapper<DTO, Entity>().withMerger(nonNullMergerFunction, createEntityFunction,
            uniqueKeyMatchingFunction);
    }

    public static <DTO, Entity> AntiMapper<DTO, Entity> meger(NullMergerFunction<DTO, Entity> nullMergerFunction,
        NonNullMergerFunction<DTO, Entity> nonNullMergerFunction,
        CreateEntityFunction<DTO, Entity> createEntityFunction,
        UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction)
    {
        return new AntiMapper<DTO, Entity>().withMerger(nullMergerFunction, nonNullMergerFunction, createEntityFunction,
            uniqueKeyMatchingFunction);
    }

    private final NullTransformerFunction<DTO, Entity> nullTransformerFunction;
    private final NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction;
    private final NullMergerFunction<DTO, Entity> nullMergerFunction;
    private final NonNullMergerFunction<DTO, Entity> nonNullMergerFunction;
    private final CreateEntityFunction<DTO, Entity> createEntityFunction;
    private final UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction;

    protected AntiMapper()
    {
        this(NullTransformerFunction.alwaysToNull(), NonNullTransformerFunction.unsupported(),
            NullMergerFunction.alwaysToNull(), NonNullMergerFunction.unsupported(), CreateEntityFunction.unsupported(),
            UniqueKeyMatchingFunction.unsupported());
    }

    protected AntiMapper(NullTransformerFunction<DTO, Entity> nullTransformerFunction,
        NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction,
        NullMergerFunction<DTO, Entity> nullMergerFunction, NonNullMergerFunction<DTO, Entity> nonNullMergerFunction,
        CreateEntityFunction<DTO, Entity> createEntityFunction,
        UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction, Object... defaultHints)
    {
        super(defaultHints);

        this.nullTransformerFunction = nullTransformerFunction;
        this.nonNullTransformerFunction = nonNullTransformerFunction;
        this.nullMergerFunction = nullMergerFunction;
        this.nonNullMergerFunction = nonNullMergerFunction;
        this.createEntityFunction = createEntityFunction;
        this.uniqueKeyMatchingFunction = uniqueKeyMatchingFunction;
    }

    public AntiMapper<DTO, Entity> withTransformer(NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction)
    {
        return new AntiMapper<>(nullTransformerFunction, nonNullTransformerFunction, nullMergerFunction,
            nonNullMergerFunction, createEntityFunction, uniqueKeyMatchingFunction);
    }

    public AntiMapper<DTO, Entity> withTransformer(NullTransformerFunction<DTO, Entity> nullTransformerFunction,
        NonNullTransformerFunction<DTO, Entity> nonNullTransformerFunction)
    {
        return new AntiMapper<>(nullTransformerFunction, nonNullTransformerFunction, nullMergerFunction,
            nonNullMergerFunction, createEntityFunction, uniqueKeyMatchingFunction);
    }

    public AntiMapper<DTO, Entity> withMerger(NonNullMergerFunction<DTO, Entity> nonNullMergerFunction,
        CreateEntityFunction<DTO, Entity> createEntityFunction,
        UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction)
    {
        return new AntiMapper<>(nullTransformerFunction, nonNullTransformerFunction, nullMergerFunction,
            nonNullMergerFunction, createEntityFunction, uniqueKeyMatchingFunction);
    }

    public AntiMapper<DTO, Entity> withMerger(NullMergerFunction<DTO, Entity> nullMergerFunction,
        NonNullMergerFunction<DTO, Entity> nonNullMergerFunction,
        CreateEntityFunction<DTO, Entity> createEntityFunction,
        UniqueKeyMatchingFunction<DTO, Entity> uniqueKeyMatchingFunction)
    {
        return new AntiMapper<>(nullTransformerFunction, nonNullTransformerFunction, nullMergerFunction,
            nonNullMergerFunction, createEntityFunction, uniqueKeyMatchingFunction);
    }

    public AntiMapper<DTO, Entity> withHints(Object... defaultHints)
    {
        if (defaultHints == null || defaultHints.length == 0)
        {
            return this;
        }

        return new AntiMapper<>(nullTransformerFunction, nonNullTransformerFunction, nullMergerFunction,
            nonNullMergerFunction, createEntityFunction, uniqueKeyMatchingFunction,
            Hints.join(this.defaultHints, defaultHints));
    }

    @Override
    protected DTO transformNull(Object[] hints)
    {
        return nullTransformerFunction.transformNull(hints);
    }

    @Override
    protected DTO transformNonNull(Entity entity, Object[] hints)
    {
        return nonNullTransformerFunction.transformNonNull(entity, hints);
    }

    @Override
    protected Entity mergeNull(Entity entity, Object[] hints)
    {
        return nullMergerFunction.mergeNull(entity, hints);
    }

    @Override
    protected Entity mergeNonNull(DTO dto, Entity entity, Object[] hints)
    {
        return nonNullMergerFunction.mergeNonNull(dto, entity, hints);
    }

    @Override
    protected Entity create(DTO dto, Object[] hints)
    {
        return createEntityFunction.create(dto, hints);
    }

    @Override
    public boolean isUniqueKeyMatching(DTO dto, Entity entity, Object... hints)
    {
        return uniqueKeyMatchingFunction.isUniqueKeyMatching(dto, entity, hints);
    }
}
