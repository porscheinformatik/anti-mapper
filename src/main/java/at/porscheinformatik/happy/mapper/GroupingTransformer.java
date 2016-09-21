package at.porscheinformatik.happy.mapper;

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
import java.util.function.Supplier;

/**
 * Adds grouping support to transformers
 *
 * @author ham
 * @param <DTO_TYPE> the dto type
 * @param <GROUP_KEY_TYPE> the type of the group key
 * @param <ENTITY_TYPE> the entity type
 */
public interface GroupingTransformer<DTO_TYPE, GROUP_KEY_TYPE, ENTITY_TYPE> extends Transformer<DTO_TYPE, ENTITY_TYPE>
{

    /**
     * Extracts the group key for the map functions
     *
     * @param entity the entity
     * @param hints optional hints
     * @return the group key
     */
    GROUP_KEY_TYPE extractGroupKey(ENTITY_TYPE entity, Object... hints);

    /**
     * Maps a collection to a map
     *
     * @param <COLLECTION_TYPE> the type of the collections in the result map
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param collectionFactory a factory for the collections in the result map
     * @param hints optional hints
     * @return a map
     */
    default <COLLECTION_TYPE extends Collection<DTO_TYPE>> Map<GROUP_KEY_TYPE, COLLECTION_TYPE> transformToGroupedMap(
        Iterable<? extends ENTITY_TYPE> entities, Supplier<Map<GROUP_KEY_TYPE, COLLECTION_TYPE>> mapFactory,
        Supplier<COLLECTION_TYPE> collectionFactory, Object... hints)
    {
        try
        {
            return MapperUtils.mapMixedGroups(entities, mapFactory.get(), this::extractGroupKey, collectionFactory,
                (entity, dto) -> false, (entity, dto) -> transform(entity, hints));
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
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     */
    default Map<GROUP_KEY_TYPE, Set<DTO_TYPE>> transformToGroupedHashSets(Iterable<? extends ENTITY_TYPE> entities,
        Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, Set<DTO_TYPE>>::new, HashSet::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     */
    default Map<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>> transformToGroupedTreeSets(
        Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>>::new, TreeSet::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a map
     */
    default Map<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>> transformToGroupedTreeSets(
        Iterable<? extends ENTITY_TYPE> entities, Comparator<? super DTO_TYPE> comparator, Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, SortedSet<DTO_TYPE>>::new,
            () -> new TreeSet<>(comparator), hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     */
    default Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformToGroupedArrayLists(Iterable<? extends ENTITY_TYPE> entities,
        Object... hints)
    {
        return transformToGroupedMap(entities, HashMap<GROUP_KEY_TYPE, List<DTO_TYPE>>::new, ArrayList::new, hints);
    }

    /**
     * Maps a collection to a map
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     */
    default Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformToUnmodifiableGroupedArrayLists(
        Iterable<? extends ENTITY_TYPE> entities, Object... hints)
    {
        Map<GROUP_KEY_TYPE, List<DTO_TYPE>> transformed = transformToGroupedArrayLists(entities, hints);

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
