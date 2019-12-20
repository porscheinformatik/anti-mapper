package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractGroupMerger<GroupKey, DTO, Entity> implements GroupMerger<DTO, Entity>
{

    private final Map<GroupKey, ? extends Iterable<? extends DTO>> dtos;
    private final Object[] hints;

    protected AbstractGroupMerger(Map<GroupKey, ? extends Iterable<? extends DTO>> dtos, Object... hints)
    {
        super();

        this.dtos = dtos;
        this.hints = hints;
    }

    protected abstract boolean isUniqueKeyMatchingNullable(DTO dto, Entity entity, Object[] hints);

    protected abstract Entity merge(DTO dto, Entity entity, Object[] hints);

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
        Map<GroupKey, ? extends Iterable<? extends DTO>> dtos = this.dtos;

        boolean keepMissing = containsHint(Hint.KEEP_MISSING);

        if (dtos == null)
        {
            boolean orEmpty = containsHint(Hint.OR_EMPTY);

            if (entities == null && !orEmpty && !keepMissing)
            {
                return null;
            }

            dtos = Collections.emptyMap();
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

            Collection<Pair<?, ? extends DTO>> pairs = new ArrayList<>();

            dtos
                .entrySet()
                .forEach(entry -> entry.getValue().forEach(item -> pairs.add(Pair.of(entry.getKey(), item))));

            boolean keepNull = containsHint(Hint.KEEP_NULL);

            entities = MapperUtils
                .mapMixed(pairs.stream(), entities,
                    (pair, entity) -> isUniqueKeyMatchingNullable(pair != null ? pair.getRight() : null, entity,
                        pair != null ? Hints.join(hints, pair.getLeft()) : hints),
                    (pair, entity) -> merge(pair != null ? pair.getRight() : null, entity,
                        pair != null ? Hints.join(hints, pair.getLeft()) : hints),
                    keepMissing, keepNull ? null : dto -> dto != null, list -> afterMergeIntoCollection(list, hints));

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge grouped DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    @Override
    public <EntityCollection extends Collection<Entity>> EntityCollection intoOrderedCollection(
        EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory)
    {
        Map<GroupKey, ? extends Iterable<? extends DTO>> dtos = this.dtos;
        boolean keepMissing = containsHint(Hint.KEEP_MISSING);

        if (dtos == null)
        {
            boolean orEmpty = containsHint(Hint.OR_EMPTY);

            if (entities == null && !orEmpty && !keepMissing)
            {
                return null;
            }

            dtos = Collections.emptyMap();
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

            Collection<Pair<?, ? extends DTO>> pairs = new ArrayList<>();

            dtos
                .entrySet()
                .forEach(entry -> entry.getValue().forEach(item -> pairs.add(Pair.of(entry.getKey(), item))));

            boolean keepNull = containsHint(Hint.KEEP_NULL);

            entities = MapperUtils
                .mapOrdered(pairs, entities,
                    (pair, entity) -> isUniqueKeyMatchingNullable(pair != null ? pair.getRight() : null, entity,
                        pair != null ? Hints.join(hints, pair.getLeft()) : hints),
                    (pair, entity) -> merge(pair != null ? pair.getRight() : null, entity,
                        pair != null ? Hints.join(hints, pair.getLeft()) : hints),
                    keepMissing, keepNull ? null : entity -> entity != null,
                    list -> afterMergeIntoCollection(list, hints));

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge grouped DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

}
