package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractStreamMerger<DTO, Entity>
{

    private final Supplier<Stream<? extends DTO>> dtos;
    private final Object[] hints;

    public AbstractStreamMerger(Supplier<Stream<? extends DTO>> dtos, Object... hints)
    {
        super();

        this.dtos = dtos;
        this.hints = hints;
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
    public <EntityCollection extends Collection<Entity>> EntityCollection mergeIntoMixedCollection(
        EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory)
    {
        if (dtos == null)
        {
            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            dtos = Collections.emptyList();
        }

        try
        {
            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);

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

            entities =
                MapperUtils.mapMixed(dtos, entities, (dto, entity) -> isUniqueKeyMatchingNullable(dto, entity, hints),
                    (dto, entity) -> merge(dto, entity, hints),
                    Hints.containsHint(hints, Hint.KEEP_NULL) ? null : dto -> dto != null,
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
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
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
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeIntoOrderedCollection(
        Iterable<? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        if (dtos == null)
        {
            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            dtos = Collections.emptyList();
        }

        try
        {
            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);

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

            entities =
                MapperUtils.mapOrdered(dtos, entities, (dto, entity) -> isUniqueKeyMatchingNullable(dto, entity, hints),
                    (dto, entity) -> merge(dto, entity, hints),
                    Hints.containsHint(hints, Hint.KEEP_NULL) ? null : entity -> entity != null,
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
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Called after a collection mapping.
     *
     * @param entities the mapped entities
     * @param hints optional hints
     */
    default void afterMergeIntoCollection(Collection<Entity> entities, Object... hints)
    {
        // intentionally left blank
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default Set<Entity> mergeIntoHashSet(Iterable<? extends DTO> dtos, Set<Entity> entities, Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities, HashSet::new, hints);
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default SortedSet<Entity> mergeIntoTreeSet(Iterable<? extends DTO> dtos, SortedSet<Entity> entities,
        Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities,
            () -> entities != null ? new TreeSet<>(entities.comparator()) : new TreeSet<>(), hints);
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set, will only be used for the create method
     * @param hints optional hints
     * @return a collection
     */
    default SortedSet<Entity> mergeIntoTreeSet(Iterable<? extends DTO> dtos, SortedSet<Entity> entities,
        Comparator<? super Entity> comparator, Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a collection to a list. If the entities parameter is null, it creates an {@link ArrayList} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default List<Entity> mergeIntoArrayList(Iterable<? extends DTO> dtos, List<Entity> entities, Object... hints)
    {
        return mergeIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    }

    /**
     * Maps a map to a collection. Ignores the order. If the entities parameter is null, it creates a {@link Collection}
     * if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case).
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoMixedCollection(
        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        if (dtos == null)
        {
            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            dtos = Collections.emptyMap();
        }

        try
        {
            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);

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

            entities = MapperUtils.mapMixed(dtos.entrySet(), entities,
                (entry, entity) -> isUniqueKeyMatchingNullable(entry != null ? entry.getValue() : null, entity,
                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
                (entry, entity) -> merge(entry != null ? entry.getValue() : null, entity,
                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : dto -> dto != null, this::afterMergeIntoCollection);

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a map to a collection. Keeps the order. If the entities parameter is null, it creates a {@link Collection}
     * if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case).
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoOrderedCollection(
        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        if (dtos == null)
        {
            if (entities == null && !Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            dtos = Collections.emptyMap();
        }

        try
        {
            boolean unmodifiable = Hints.containsHint(hints, Hint.UNMODIFIABLE);

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

            entities = MapperUtils.mapOrdered(dtos.entrySet(), entities,
                (entry, entity) -> isUniqueKeyMatchingNullable(entry != null ? entry.getValue() : null, entity,
                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
                (entry, entity) -> merge(entry != null ? entry.getValue() : null, entity,
                    entry != null ? Hints.join(hints, entry.getKey()) : hints),
                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : entity -> entity != null,
                this::afterMergeIntoCollection);

            if (unmodifiable)
            {
                entities = MapperUtils.toUnmodifiableCollection(entities);
            }

            return entities;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a map to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary. Ignores
     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default Set<Entity> mergeMapIntoHashSet(Map<?, ? extends DTO> dtos, Set<Entity> entities, Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities, HashSet::new, hints);
    }

    /**
     * Maps a map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary. Ignores
     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a sorted set
     */
    default SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
        Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities,
            () -> entities != null ? new TreeSet<>(entities.comparator()) : new TreeSet<>(), hints);
    }

    /**
     * Maps a map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary. Ignores
     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a sorted set
     */
    default SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
        Comparator<? super Entity> comparator, Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a map to a list. If the entities parameter is null, it creates a {@link List} if necessary. Ignores DTOs
     * that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default List<Entity> mergeMapIntoArrayList(Map<?, ? extends DTO> dtos, List<Entity> entities, Object... hints)
    {
        return mergeMapIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    }

}
