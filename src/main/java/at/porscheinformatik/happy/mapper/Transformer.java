package at.porscheinformatik.happy.mapper;

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
     * Maps the entity to a DTO. Be aware, that the passed entity may be null!
     *
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the DTO, may be null if the entity is null
     */
    DTO_TYPE transform(ENTITY_TYPE entity, Object... hints);

    /**
     * Maps a collection to a collection
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

        try
        {
            return StreamSupport.stream(entities.spliterator(), false).map(entity -> transform(entity, hints)).collect(
                Collectors.toCollection(dtoCollectionFactory));
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a collection: %s", e,
                MapperUtils.abbreviate(String.valueOf(entities), 4096));
        }
    }

    /**
     * Maps a collection to a set
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
     * Maps a collection to a set
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
     * Maps a collection to a set
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
     * Maps a collection to a list
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a list
     */
    default List<DTO_TYPE> transformToArrayList(Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        return transformToCollection(entities, ArrayList::new, hints);
    }

    default List<DTO_TYPE> transformToUnmodifiableList(Iterable<? extends ENTITY_TYPE> entities, Object[] hints)
    {
        List<DTO_TYPE> transformed = transformToArrayList(entities, hints);

        return transformed != null ? Collections.unmodifiableList(transformed) : null;
    }

    /**
     * Maps a collection to a map
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

                result.put(key, dto);
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
     * Maps a collection to a map
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

}
