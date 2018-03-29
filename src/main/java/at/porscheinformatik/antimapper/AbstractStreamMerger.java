package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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

    /**
     * Maps a collection to a collection. Ignores the order. If the entities parameter is null, it creates a
     * {@link Collection} if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in
     * this case, merging the entities). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @return a collection
     */
    @Override
    public <EntityCollection extends Collection<Entity>> EntityCollection intoMixedCollection(EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory)
    {
        Stream<? extends DTOContainer> dtoContainers = streamSupplier.get();

        if (dtoContainers == null)
        {
            if (entities == null && !containsHint(Hint.OR_EMPTY))
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

            entities = MapperUtils.mapMixed(dtoContainers, entities,
                (dtoContainer, entity) -> isUniqueKeyMatchingNullable(dtoContainer, entity, hints),
                (dtoContainer, entity) -> merge(dtoContainer, entity, hints),
                containsHint(Hint.KEEP_NULL) ? null : dto -> dto != null,
                list -> afterMergeIntoCollection(list, hints));

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

    /**
     * Maps a collection to a collection. Keeps the order. If the entities parameter is null, it creates a
     * {@link Collection} if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in
     * this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @return a collection
     */
    @Override
    public <EntityCollection extends Collection<Entity>> EntityCollection intoOrderedCollection(
        EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory)
    {
        Stream<? extends DTOContainer> dtoContainers = streamSupplier.get();

        if (dtoContainers == null)
        {
            if (entities == null && !containsHint(Hint.OR_EMPTY))
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

            entities = MapperUtils.mapOrdered(dtoContainers, entities,
                (dto, entity) -> isUniqueKeyMatchingNullable(dto, entity, hints),
                (dtoContainer, entity) -> merge(dtoContainer, entity, hints),
                containsHint(Hint.KEEP_NULL) ? null : entity -> entity != null,
                list -> afterMergeIntoCollection(list, hints));

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

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @return a collection
     */
    @Override
    public Set<Entity> intoHashSet(Set<Entity> entities)
    {
        return intoMixedCollection(entities, HashSet::new);
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @return a collection
     */
    @Override
    public SortedSet<Entity> intoTreeSet(SortedSet<Entity> entities)
    {
        return intoMixedCollection(entities,
            () -> entities != null ? new TreeSet<>(entities.comparator()) : new TreeSet<>());
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set, will only be used for the create method
     * @return a collection
     */
    @Override
    public SortedSet<Entity> intoTreeSet(SortedSet<Entity> entities, Comparator<? super Entity> comparator)
    {
        return intoMixedCollection(entities, () -> new TreeSet<>(comparator));
    }

    /**
     * Maps a collection to a list. If the entities parameter is null, it creates an {@link ArrayList} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @return a collection
     */
    @Override
    public List<Entity> intoArrayList(List<Entity> entities)
    {
        return intoOrderedCollection(entities, ArrayList::new);
    }

    //    /**
    //     * Maps a map to a collection. Ignores the order. If the entities parameter is null, it creates a {@link Collection}
    //     * if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
    //     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case).
    //     * Never returns null if the {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param <EntityCollection> the type of the collection
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param entityCollectionFactory a factory for the needed collection
    //     * @param hints optional hints
    //     * @return a collection
    //     */
    //    public <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoMixedCollection(
    //        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
    //        Object... hints)
    //    {
    //        if (dtos == null)
    //        {
    //            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
    //            {
    //                return null;
    //            }
    //
    //            dtos = Collections.emptyMap();
    //        }
    //
    //        try
    //        {
    //            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);
    //
    //            if (entities == null)
    //            {
    //                entities = entityCollectionFactory.get();
    //            }
    //            else if (unmodifiable)
    //            {
    //                EntityCollection originalEntity = entities;
    //
    //                entities = entityCollectionFactory.get();
    //                entities.addAll(originalEntity);
    //            }
    //
    //            entities = MapperUtils.mapMixed(dtos.entrySet(), entities,
    //                (entry, entity) -> isUniqueKeyMatchingNullable(entry != null ? entry.getValue() : null, entity,
    //                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
    //                (entry, entity) -> merge(entry != null ? entry.getValue() : null, entity,
    //                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
    //                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : dto -> dto != null, this::afterMergeIntoCollection);
    //
    //            if (unmodifiable)
    //            {
    //                entities = MapperUtils.toUnmodifiableCollection(entities);
    //            }
    //
    //            return entities;
    //        }
    //        catch (Exception e)
    //        {
    //            throw new MapperException("Failed to merge DTOs into a mixed collection: %s => %s", e,
    //                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
    //                MapperUtils.abbreviate(String.valueOf(entities), 4096));
    //        }
    //    }
    //
    //    /**
    //     * Maps a map to a collection. Keeps the order. If the entities parameter is null, it creates a {@link Collection}
    //     * if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
    //     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case).
    //     * Never returns null if the {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param <EntityCollection> the type of the collection
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param entityCollectionFactory a factory for the needed collection
    //     * @param hints optional hints
    //     * @return a collection
    //     */
    //    public <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoOrderedCollection(
    //        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
    //        Object... hints)
    //    {
    //        if (dtos == null)
    //        {
    //            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
    //            {
    //                return null;
    //            }
    //
    //            dtos = Collections.emptyMap();
    //        }
    //
    //        try
    //        {
    //            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);
    //
    //            if (entities == null)
    //            {
    //                entities = entityCollectionFactory.get();
    //            }
    //            else if (unmodifiable)
    //            {
    //                EntityCollection originalEntity = entities;
    //
    //                entities = entityCollectionFactory.get();
    //                entities.addAll(originalEntity);
    //            }
    //
    //            entities = MapperUtils.mapOrdered(dtos.entrySet(), entities,
    //                (entry, entity) -> isUniqueKeyMatchingNullable(entry != null ? entry.getValue() : null, entity,
    //                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
    //                (entry, entity) -> merge(entry != null ? entry.getValue() : null, entity,
    //                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
    //                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : entity -> entity != null,
    //                this::afterMergeIntoCollection);
    //
    //            if (unmodifiable)
    //            {
    //                entities = MapperUtils.toUnmodifiableCollection(entities);
    //            }
    //
    //            return entities;
    //        }
    //        catch (Exception e)
    //        {
    //            throw new MapperException("Failed to merge DTOs into an ordered collection: %s => %s", e,
    //                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
    //                MapperUtils.abbreviate(String.valueOf(entities), 4096));
    //        }
    //    }
    //
    //    /**
    //     * Maps a map to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary. Ignores
    //     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
    //     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
    //     * {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param hints optional hints
    //     * @return a collection
    //     */
    //    public Set<Entity> mergeMapIntoHashSet(Map<?, ? extends DTO> dtos, Set<Entity> entities, Object... hints)
    //    {
    //        return mergeMapIntoMixedCollection(dtos, entities, HashSet::new, hints);
    //    }
    //
    //    /**
    //     * Maps a map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary. Ignores
    //     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
    //     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
    //     * {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param hints optional hints
    //     * @return a sorted set
    //     */
    //    public SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
    //        Object... hints)
    //    {
    //        return mergeMapIntoMixedCollection(dtos, entities,
    //            () -> entities != null ? new TreeSet<>(entities.comparator()) : new TreeSet<>(), hints);
    //    }
    //
    //    /**
    //     * Maps a map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary. Ignores
    //     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
    //     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
    //     * {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param comparator the comparator for the tree set
    //     * @param hints optional hints
    //     * @return a sorted set
    //     */
    //    public SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
    //        Comparator<? super Entity> comparator, Object... hints)
    //    {
    //        return mergeMapIntoMixedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    //    }
    //
    //    /**
    //     * Maps a map to a list. If the entities parameter is null, it creates a {@link List} if necessary. Ignores DTOs
    //     * that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
    //     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
    //     * {@link Hint#OR_EMPTY} is set.
    //     *
    //     * @param dtos the DTOs, may be null
    //     * @param entities the entities, may be null
    //     * @param hints optional hints
    //     * @return a collection
    //     */
    //    public List<Entity> mergeMapIntoArrayList(Map<?, ? extends DTO> dtos, List<Entity> entities, Object... hints)
    //    {
    //        return mergeMapIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    //    }

}
