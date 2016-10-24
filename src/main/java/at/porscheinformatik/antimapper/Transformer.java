package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A transformer is a mapper that (usually) transforms an entity to a DTO. The DTO is always a new object, there is no
 * need to updated an already existing one.
 *
 * @author ham
 * @param <DTO_TYPE> the type of the DTO
 * @param <ENTITY_TYPE> the type of the entity
 */
public interface Transformer<DTO_TYPE, ENTITY_TYPE> extends Referer<DTO_TYPE, ENTITY_TYPE>
{

    /**
     * Transforms the entity to a DTO. Be aware, that the passed entity may be null!
     *
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the DTO, may be null if the entity is null
     */
    DTO_TYPE transform(ENTITY_TYPE entity, Object... hints);

    /**
     * Transforms the entities in the {@link Stream} to DTOs and returns the {@link Stream} with DTOs. Ignores entities
     * that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     */
    default Stream<DTO_TYPE> transformEach(Stream<? extends ENTITY_TYPE> entities, Object... hints)
    {
        if (entities == null)
        {
            return null;
        }

        try
        {
            Stream<DTO_TYPE> stream = entities.map(entity -> transform(entity, hints));

            if (!Hints.containsHint(hints, Hint.KEEP_NULL))
            {
                stream = stream.filter(dto -> dto != null);
            }

            return stream;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities in stream: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Transforms the entities in the {@link Collection} to DTOs and returns the {@link Stream} with DTOs. Removes
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     */
    default Stream<DTO_TYPE> transformToStream(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        if (entities == null)
        {
            return null;
        }

        return transformEach(StreamSupport.stream(entities.spliterator(), false), hints);
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link Collection} of DTOs. The {@link Collection} of DTOs will
     * be created by the specified factory. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL}
     * hint is set.
     *
     * @param <DTO_COLLECTION_TYPE> the type of the collection of DTOs
     * @param entities the entities, may be null
     * @param dtoCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <DTO_COLLECTION_TYPE extends Collection<DTO_TYPE>> DTO_COLLECTION_TYPE transformToCollection(
        Iterable<? extends ENTITY_TYPE> entities, Supplier<DTO_COLLECTION_TYPE> dtoCollectionFactory, Object... hints)
    {
        if (entities == null)
        {
            return null;
        }

        return transformToStream(entities, hints).collect(Collectors.toCollection(dtoCollectionFactory));
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link HashSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     */
    default Set<DTO_TYPE> transformToHashSet(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        return transformToCollection(entities, HashSet::new, hints);
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link SortedSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     */
    default SortedSet<DTO_TYPE> transformToTreeSet(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        return transformToCollection(entities, TreeSet::new, hints);
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link SortedSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a set
     */
    default SortedSet<DTO_TYPE> transformToTreeSet(Iterable<? extends ENTITY_TYPE> entities,
        Comparator<? super DTO_TYPE> comparator, Object... hints)
    {
        return transformToCollection(entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Transforms a {@link Collection} of entities to an {@link ArrayList} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a list
     */
    default List<DTO_TYPE> transformToArrayList(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        return transformToCollection(entities, ArrayList::new, hints);
    }

    /**
     * Transforms a {@link Collection} of entities to an unmodifiable {@link ArrayList} of DTOs. Ignores entities that
     * transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a list
     */
    default List<DTO_TYPE> transformToUnmodifiableArrayList(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        List<DTO_TYPE> transformed = transformToArrayList(entities, hints);

        return transformed != null ? Collections.unmodifiableList(transformed) : null;
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link Map} of DTOs. Ignores entities that transform to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key will
     * overwrite each other.
     *
     * @param <KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param keyFunction the function to extract the key from one entity
     * @param hints optional hints
     * @return a map
     */
    default <KEY_TYPE> Map<KEY_TYPE, DTO_TYPE> transformToMap(Iterable<? extends ENTITY_TYPE> entities,
        Supplier<Map<KEY_TYPE, DTO_TYPE>> mapFactory, Function<ENTITY_TYPE, KEY_TYPE> keyFunction, Object... hints)
    {
        if (entities == null)
        {
            return null;
        }

        boolean keepNull = Hints.containsHint(hints, Hint.KEEP_NULL);

        try
        {
            Map<KEY_TYPE, DTO_TYPE> result = new HashMap<>();

            for (ENTITY_TYPE entity : entities)
            {
                if (entity == null)
                {
                    continue;
                }

                KEY_TYPE key = keyFunction.apply(entity);
                DTO_TYPE dto = transform(entity, hints);

                if (dto != null || keepNull)
                {
                    result.put(key, dto);
                }
            }

            return result;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a map: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Transforms a {@link Collection} of entities to a {@link HashMap} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key
     * will overwrite each other.
     *
     * @param <KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param keyFunction the function to extract the key from one entity
     * @param hints optional hints
     * @return a map
     */
    default <KEY_TYPE> Map<KEY_TYPE, DTO_TYPE> transformToHashMap(Iterable<? extends ENTITY_TYPE> entities,
        Function<ENTITY_TYPE, KEY_TYPE> keyFunction, Object... hints)
    {
        return transformToMap(entities, HashMap<KEY_TYPE, DTO_TYPE>::new, keyFunction, hints);
    }

    /**
     * Transforms a {@link Collection} of entities to a grouped {@link Map} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set.
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param <COLLECTION_TYPE> the type of the collections in the result map
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param groupKeyFunction extracts the key for the map
     * @param collectionFactory a factory for the collections in the result map
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE, COLLECTION_TYPE extends Collection<DTO_TYPE>> Map<GROUP_KEY_TYPE, COLLECTION_TYPE> transformToGroupedMap(
        Iterable<? extends ENTITY_TYPE> entities, Supplier<Map<GROUP_KEY_TYPE, COLLECTION_TYPE>> mapFactory,
        Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction, Supplier<COLLECTION_TYPE> collectionFactory,
        Object... hints)
    {
        try
        {
            return MapperUtils.mapMixedGroups(entities, mapFactory.get(), groupKeyFunction, collectionFactory,
                (entity, dto) -> false, (entity, dto) -> transform(entity, hints),
                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : dto -> dto != null, null);
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a grouped map: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a collection to a map
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE> Map<GROUP_KEY_TYPE, Set<DTO_TYPE>> transformToGroupedHashSets(
        Iterable<? extends ENTITY_TYPE> entities, Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction,
        Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, Set<DTO_TYPE>>::new, groupKeyFunction,
            HashSet::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE> Map<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>> transformToGroupedTreeSets(
        Iterable<? extends ENTITY_TYPE> entities, Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction,
        Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>>::new, groupKeyFunction,
            TreeSet::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE> Map<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>> transformToGroupedTreeSets(
        Iterable<? extends ENTITY_TYPE> entities, Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction,
        Comparator<? super DTO_TYPE> comparator, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>>::new, groupKeyFunction,
            () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE> Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformToGroupedArrayLists(
        Iterable<? extends ENTITY_TYPE> entities, Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction,
        Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, List<DTO_TYPE>>::new, groupKeyFunction,
            ArrayList::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param <GROUP_KEY_TYPE> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GROUP_KEY_TYPE> Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformToUnmodifiableGroupedArrayLists(
        Iterable<? extends ENTITY_TYPE> entities, Function<ENTITY_TYPE, GROUP_KEY_TYPE> groupKeyFunction,
        Object... hints)
    {
        Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformed =
            transformToGroupedArrayLists(entities, groupKeyFunction, hints);

        if (transformed == null)
        {
            return null;
        }

        Map<GROUP_KEY_TYPE, List<DTO_TYPE>> unmodifiable = new HashMap<>();

        for (Entry<GROUP_KEY_TYPE, List<DTO_TYPE>> entry : transformed.entrySet())
        {
            unmodifiable.put(entry.getKey(),
                entry.getValue() != null ? Collections.unmodifiableList(entry.getValue()) : null);
        }

        return Collections.unmodifiableMap(unmodifiable);
    }

}
