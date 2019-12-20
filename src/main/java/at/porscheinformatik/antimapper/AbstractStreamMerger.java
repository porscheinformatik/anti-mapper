package at.porscheinformatik.antimapper;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractStreamMerger<DTO, DTOContainer, Entity> implements StreamMerger<DTO, Entity>
{

    private final Supplier<Stream<? extends DTOContainer>> streamSupplier;
    private final Object[] hints;

    protected AbstractStreamMerger(Supplier<Stream<? extends DTOContainer>> streamSupplier, Object... hints)
    {
        super();

        this.streamSupplier = streamSupplier;
        this.hints = hints;
    }

    protected abstract boolean isUniqueKeyMatchingNullable(DTOContainer dtoContainer, Entity entity, Object[] hints);

    protected abstract Entity merge(DTOContainer dtoContainer, Entity entity, Object[] hints);

    protected abstract void afterMergeIntoCollection(Collection<Entity> entities, Object[] hints);

    protected abstract Object[] getTransformerHints();

    protected boolean containsHint(Object object)
    {
        return Hints.containsHint(hints, object) || Hints.containsHint(getTransformerHints(), object);
    }

    @Override
    public <EntityCollection extends Collection<Entity>> EntityCollection intoMixedCollection(EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory)
    {
        Stream<? extends DTOContainer> dtoContainers = streamSupplier.get();
        boolean keepMissing = containsHint(Hint.KEEP_MISSING);

        if (dtoContainers == null)
        {
            boolean orEmpty = containsHint(Hint.OR_EMPTY);

            if (entities == null && !orEmpty && !keepMissing)
            {
                return null;
            }

            dtoContainers = Stream.empty();
        }

        try
        {
            boolean unmodifiable = containsHint(Hint.UNMODIFIABLE);

            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }
            else if (unmodifiable)
            {
                EntityCollection originalEntity = entities;

                entities = entityCollectionFactory.get();
                entities.addAll(originalEntity);
            }

            boolean keepNull = containsHint(Hint.KEEP_NULL);

            entities = MapperUtils
                .mapMixed(dtoContainers, entities,
                    (dtoContainer, entity) -> isUniqueKeyMatchingNullable(dtoContainer, entity, hints),
                    (dtoContainer, entity) -> merge(dtoContainer, entity, hints), keepMissing,
                    keepNull ? null : dto -> dto != null, list -> afterMergeIntoCollection(list, hints));

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtoContainers), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    @Override
    public <EntityCollection extends Collection<Entity>> EntityCollection intoOrderedCollection(
        EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory)
    {
        Stream<? extends DTOContainer> dtoContainers = streamSupplier.get();
        boolean keepMissing = containsHint(Hint.KEEP_MISSING);

        if (dtoContainers == null)
        {
            boolean orEmpty = containsHint(Hint.OR_EMPTY);

            if (entities == null && !orEmpty && !keepMissing)
            {
                return null;
            }

            dtoContainers = Stream.empty();
        }

        try
        {
            boolean unmodifiable = containsHint(Hint.UNMODIFIABLE);

            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }
            else if (unmodifiable)
            {
                EntityCollection originalEntity = entities;

                entities = entityCollectionFactory.get();
                entities.addAll(originalEntity);
            }

            boolean keepNull = containsHint(Hint.KEEP_NULL);

            entities = MapperUtils
                .mapOrdered(dtoContainers, entities, (dto, entity) -> isUniqueKeyMatchingNullable(dto, entity, hints),
                    (dtoContainer, entity) -> merge(dtoContainer, entity, hints), keepMissing,
                    keepNull ? null : entity -> entity != null, list -> afterMergeIntoCollection(list, hints));

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtoContainers), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

}
