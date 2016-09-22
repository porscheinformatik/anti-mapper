package at.porscheinformatik.happy.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * A merger is a mapper that merges a DTO into an entity.
 *
 * @author ham
 * @param <DTO_TYPE> the dto type
 * @param <ENTITY_TYPE> the entity type
 */
public interface Merger<DTO_TYPE, ENTITY_TYPE>
{

    /**
     * Maps the DTO to an entity. The implementation must be able to cope with a null value passed as entity! The
     * implementation should prefer to modify a passed entity instead of creating a new one, but must not do so.
     *
     * @param dto the dto, may be null
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the entity, either the passed one, or a newly created one
     */
    ENTITY_TYPE merge(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);

    /**
     * Returns true if the unique keys match. Most often, this is the id. The method will not be called if either or
     * both values are null. The method will be used for searches during list merge operations. The methods themself
     * ensure, that no DTO and no entity will be matched twice, thus comparing the id should be enough most of the time,
     * even if the list may contain multiple entries with an id set to null.
     *
     * If the entity is identifiable by a specific (combined) key, then use this key for the match operation. If this it
     * not the case, just use the id.
     *
     * @param dto the DTO, never null
     * @param entity the entity, never null
     * @param hints optional hints
     * @return true on match
     */
    boolean isUniqueKeyMatching(DTO_TYPE dto, ENTITY_TYPE entity, Object... hints);

    /**
     * Maps a collection to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeIntoMixedCollection(
        Iterable<? extends DTO_TYPE> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            return MapperUtils.mapMixed(dtos, entities, this::isUniqueKeyMatching,
                (dto, entity) -> merge(dto, entity, hints), list -> afterMergeIntoCollection(list, hints));
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a collection to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeIntoOrderedCollection(
        Iterable<? extends DTO_TYPE> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            return MapperUtils.mapOrdered(dtos, entities, this::isUniqueKeyMatching,
                (dto, entity) -> merge(dto, entity, hints), list -> afterMergeIntoCollection(list, hints));
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Called after a collection mapping
     *
     * @param entities the mapped entities
     * @param hints optional hints
     */
    default void afterMergeIntoCollection(Collection<ENTITY_TYPE> entities, Object... hints)
    {
        // intentionally left blank
    }

    /**
     * Maps a collection to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default Set<ENTITY_TYPE> mergeIntoHashSet(Iterable<? extends DTO_TYPE> dtos, Set<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities, HashSet::new, hints);
    }

    /**
     * Maps a collection to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default SortedSet<ENTITY_TYPE> mergeIntoTreeSet(Iterable<? extends DTO_TYPE> dtos, SortedSet<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities, TreeSet::new, hints);
    }

    /**
     * Maps a collection to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a collection
     */
    default SortedSet<ENTITY_TYPE> mergeIntoTreeSet(Iterable<? extends DTO_TYPE> dtos, SortedSet<ENTITY_TYPE> entities,
        Comparator<? super ENTITY_TYPE> comparator, Object... hints)
    {
        return mergeIntoMixedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a collection to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default List<ENTITY_TYPE> mergeIntoArrayList(Iterable<? extends DTO_TYPE> dtos, List<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    }

    /**
     * Maps a map to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeMapIntoMixedCollection(
        Map<?, ? extends DTO_TYPE> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            return MapperUtils.mapMixed(dtos.entrySet(), entities,
                (entry, entity) -> isUniqueKeyMatching(entry.getValue(), entity, Hints.join(hints, entry.getKey())),
                (entry, entity) -> merge(entry.getValue(), entity, Hints.join(hints, entry.getKey())),
                this::afterMergeIntoCollection);
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a map to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeMapIntoOrderedCollection(
        Map<?, ? extends DTO_TYPE> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            return MapperUtils.mapOrdered(dtos.entrySet(), entities,
                (entry, entity) -> isUniqueKeyMatching(entry.getValue(), entity, Hints.join(hints, entry.getKey())),
                (entry, entity) -> merge(entry.getValue(), entity, Hints.join(hints, entry.getKey())),
                this::afterMergeIntoCollection);
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a map to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default Set<ENTITY_TYPE> mergeMapIntoHashSet(Map<?, ? extends DTO_TYPE> dtos, Set<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities, HashSet::new, hints);
    }

    /**
     * Maps a map to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a sorted set
     */
    default SortedSet<ENTITY_TYPE> mergeMapIntoTreeSet(Map<?, ? extends DTO_TYPE> dtos, SortedSet<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities, TreeSet::new, hints);
    }

    /**
     * Maps a map to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a sorted set
     */
    default SortedSet<ENTITY_TYPE> mergeMapIntoTreeSet(Map<?, ? extends DTO_TYPE> dtos, SortedSet<ENTITY_TYPE> entities,
        Comparator<? super ENTITY_TYPE> comparator, Object... hints)
    {
        return mergeMapIntoMixedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a map to a collection
     *
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a collection
     */
    default List<ENTITY_TYPE> mergeMapIntoArrayList(Map<?, ? extends DTO_TYPE> dtos, List<ENTITY_TYPE> entities,
        Object... hints)
    {
        return mergeMapIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    }

    /**
     * Maps a grouped map to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param entityCollectionFactory the factory for the entities collection
     * @param hints optional hints
     * @return the collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeGroupedMapIntoMixedCollection(
        Map<?, ? extends Collection<? extends DTO_TYPE>> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            Collection<Pair<?, ? extends DTO_TYPE>> pairs = new LinkedHashSet<>();

            dtos.entrySet().forEach(
                entry -> entry.getValue().forEach(item -> pairs.add(Pair.of(entry.getKey(), item))));

            return MapperUtils.mapMixed(pairs, entities,
                (pair, entity) -> isUniqueKeyMatching(pair.getRight(), entity, Hints.join(hints, pair.getLeft())),
                (pair, entity) -> merge(pair.getRight(), entity, Hints.join(hints, pair.getLeft())),
                list -> afterMergeIntoCollection(list, hints));
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge grouped DTOs into a mixed collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a grouped map to a collection
     *
     * @param <ENTITY_COLLECTION_TYPE> the type of the collection
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param entityCollectionFactory the factory for the entities collection
     * @param hints optional hints
     * @return the collection
     */
    default <ENTITY_COLLECTION_TYPE extends Collection<ENTITY_TYPE>> ENTITY_COLLECTION_TYPE mergeGroupedMapIntoOrderedCollection(
        Map<?, ? extends Collection<? extends DTO_TYPE>> dtos, ENTITY_COLLECTION_TYPE entities,
        Supplier<ENTITY_COLLECTION_TYPE> entityCollectionFactory, Object... hints)
    {
        if (dtos == null)
        {
            if (entities != null)
            {
                entities.clear();
            }

            return entities;
        }

        try
        {
            if (entities == null)
            {
                entities = entityCollectionFactory.get();
            }

            Collection<Pair<?, ? extends DTO_TYPE>> pairs = new LinkedHashSet<>();

            dtos.entrySet().forEach(
                entry -> entry.getValue().forEach(item -> pairs.add(Pair.of(entry.getKey(), item))));

            return MapperUtils.mapOrdered(pairs, entities,
                (pair, entity) -> isUniqueKeyMatching(pair.getRight(), entity, Hints.join(hints, pair.getLeft())),
                (pair, entity) -> merge(pair.getRight(), entity, Hints.join(hints, pair.getLeft())),
                list -> afterMergeIntoCollection(list, hints));
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to merge grouped DTOs into an ordered collection: %s => %s", e,
                MapperUtils.abbreviate(String.valueOf(dtos), 4096),
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a grouped map to a set
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     */
    default Set<ENTITY_TYPE> mergeGroupedMapIntoHashSet(Map<?, ? extends Collection<? extends DTO_TYPE>> dtos,
        Set<ENTITY_TYPE> entities, Object... hints)
    {
        return mergeGroupedMapIntoMixedCollection(dtos, entities, HashSet::new, hints);
    }

    /**
     * Maps a grouped map to a set
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     */
    default SortedSet<ENTITY_TYPE> mergeGroupedMapIntoTreeSet(Map<?, ? extends Collection<? extends DTO_TYPE>> dtos,
        SortedSet<ENTITY_TYPE> entities, Object... hints)
    {
        return mergeGroupedMapIntoOrderedCollection(dtos, entities, TreeSet::new, hints);
    }

    /**
     * Maps a grouped map to a set
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param comparator the comparator
     * @param hints optional hints
     * @return the collection
     */
    default SortedSet<ENTITY_TYPE> mergeGroupedMapIntoTreeSet(Map<?, ? extends Collection<? extends DTO_TYPE>> dtos,
        SortedSet<ENTITY_TYPE> entities, Comparator<? super ENTITY_TYPE> comparator, Object... hints)
    {
        return mergeGroupedMapIntoOrderedCollection(dtos, entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a grouped map to a list
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     */
    default List<ENTITY_TYPE> mergeGroupedMapIntoArrayList(Map<?, ? extends Collection<? extends DTO_TYPE>> dtos,
        List<ENTITY_TYPE> entities, Object... hints)
    {
        return mergeGroupedMapIntoOrderedCollection(dtos, entities, ArrayList::new, hints);
    }

}
