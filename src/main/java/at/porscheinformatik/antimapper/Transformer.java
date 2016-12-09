package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
public interface Transformer<DTO, Entity>
{

    /**
     * Transforms the entity to a DTO. Be aware, that the passed entity may be null!
     *
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the DTO, may be null if the entity is null
     */
    DTO transform(Entity entity, Object... hints);

    /**
     * Transforms the entities in the {@link Stream} to DTOs and returns the {@link Stream} with DTOs. Ignores entities
     * that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     */
    default Stream<DTO> transformEach(Stream<? extends Entity> entities, Object... hints)
    {
        if (entities == null)
        {
            if (Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return Stream.empty();
            }

            return null;
        }

        try
        {
            Stream<DTO> stream = entities.map(entity -> transform(entity, hints));

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
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     */
    default Stream<DTO> transformToStream(Iterable<? extends Entity> entities, Object... hints)
    {
        if (entities == null)
        {
            if (Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return Stream.empty();
            }

            return null;
        }

        return transformEach(StreamSupport.stream(entities.spliterator(), false), hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link Collection} of DTOs. The {@link Collection} of DTOs will
     * be created by the specified factory. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL}
     * hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param <DTOCollection> the type of the collection of DTOs
     * @param entities the entities, may be null
     * @param dtoCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     */
    default <DTOCollection extends Collection<DTO>> DTOCollection transformToCollection(
        Iterable<? extends Entity> entities, Supplier<DTOCollection> dtoCollectionFactory, Object... hints)
    {
        if (entities == null)
        {
            if (!Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            entities = Collections.emptyList();
        }

        DTOCollection dtos = transformToStream(entities, hints).collect(Collectors.toCollection(dtoCollectionFactory));

        if (Hints.containsHint(hints, Hint.UNMODIFIABLE))
        {
            dtos = MapperUtils.toUnmodifiableCollection(dtos);
        }

        return dtos;
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link HashSet} of DTOs. Ignores entities that transform to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     */
    default Set<DTO> transformToHashSet(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformToCollection(entities, HashSet::new, hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link SortedSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     */
    default SortedSet<DTO> transformToTreeSet(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformToCollection(entities, TreeSet::new, hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link SortedSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a set
     */
    default SortedSet<DTO> transformToTreeSet(Iterable<? extends Entity> entities, Comparator<? super DTO> comparator,
        Object... hints)
    {
        return transformToCollection(entities, () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to an {@link ArrayList} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a list
     */
    default List<DTO> transformToArrayList(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformToCollection(entities, ArrayList::new, hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link Map} of DTOs. Ignores entities that transform to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key will
     * overwrite each other. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns
     * null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <Key> the type of the key
     * @param <DTOMap> the type of map
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param keyFunction the function to extract the key from one entity
     * @param hints optional hints
     * @return a map
     */
    default <Key, DTOMap extends Map<Key, DTO>> DTOMap transformToMap(Iterable<? extends Entity> entities,
        Supplier<DTOMap> mapFactory, Function<Entity, Key> keyFunction, Object... hints)
    {
        if (entities == null)
        {
            if (!Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            entities = Collections.emptyList();
        }

        boolean keepNull = Hints.containsHint(hints, Hint.KEEP_NULL);

        try
        {
            DTOMap dtos = mapFactory.get();

            for (Entity entity : entities)
            {
                if (entity == null)
                {
                    continue;
                }

                Key key = keyFunction.apply(entity);
                DTO dto = transform(entity, hints);

                if (dto != null || keepNull)
                {
                    dtos.put(key, dto);
                }
            }

            if (Hints.containsHint(hints, Hint.UNMODIFIABLE))
            {
                dtos = MapperUtils.toUnmodifiableMap(dtos);
            }

            return dtos;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a map: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link HashMap} of DTOs. Ignores entities that transform to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key will
     * overwrite each other. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns
     * null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <Key> the type of the key
     * @param entities the entities, may be null
     * @param keyFunction the function to extract the key from one entity
     * @param hints optional hints
     * @return a map
     */
    default <Key> Map<Key, DTO> transformToHashMap(Iterable<? extends Entity> entities,
        Function<Entity, Key> keyFunction, Object... hints)
    {
        return transformToMap(entities, HashMap<Key, DTO>::new, keyFunction, hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a grouped {@link Map} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param <DTOCollection> the type of the collections in the result map
     * @param <DTOMap> the type of map
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param groupKeyFunction extracts the key for the map
     * @param collectionFactory a factory for the collections in the result map
     * @param hints optional hints
     * @return a map
     */
    default <GroupKey, DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> transformToGroupedMap(
        Iterable<? extends Entity> entities, Supplier<DTOMap> mapFactory, Function<Entity, GroupKey> groupKeyFunction,
        Supplier<DTOCollection> collectionFactory, Object... hints)
    {
        if (entities == null)
        {
            if (!Hints.containsHint(hints, Hint.OR_EMPTY))
            {
                return null;
            }

            entities = Collections.emptyList();
        }

        try
        {
            Map<GroupKey, DTOCollection> dtos = MapperUtils.mapMixedGroups(entities, mapFactory.get(), groupKeyFunction,
                collectionFactory, (entity, dto) -> false, (entity, dto) -> transform(entity, hints),
                Hints.containsHint(hints, Hint.KEEP_NULL) ? null : dto -> dto != null, map -> {
                    if (Hints.containsHint(hints, Hint.UNMODIFIABLE))
                    {
                        List<GroupKey> keys = new ArrayList<>(map.keySet());

                        keys.forEach(key -> map.put(key, MapperUtils.toUnmodifiableCollection(map.get(key))));
                    }
                });

            if (Hints.containsHint(hints, Hint.UNMODIFIABLE))
            {
                dtos = MapperUtils.toUnmodifiableMap(dtos);
            }

            return dtos;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a grouped map: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Transforms an {@link Iterable} of entities to a grouped {@link Map} of {@link HashSet}s with DTOs. Ignores
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GroupKey> Map<GroupKey, Set<DTO>> transformToGroupedHashSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GroupKey, Set<DTO>>::new, groupKeyFunction, HashSet::new, hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a grouped {@link Map} of {@link TreeSet}s with DTOs. Ignores
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformToGroupedTreeSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction, TreeSet::new,
            hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a grouped {@link Map} of {@link TreeSet}s with DTOs. Ignores
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a map
     */
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformToGroupedTreeSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Comparator<? super DTO> comparator, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction,
            () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Transforms an {@link Iterable} of entities to a grouped {@link Map} of {@link ArrayList}s with DTOs. Ignores
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param groupKeyFunction extracts the key for the map
     * @param hints optional hints
     * @return a map
     */
    default <GroupKey> Map<GroupKey, List<DTO>> transformToGroupedArrayLists(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GroupKey, List<DTO>>::new, groupKeyFunction, ArrayList::new,
            hints);
    }

}
