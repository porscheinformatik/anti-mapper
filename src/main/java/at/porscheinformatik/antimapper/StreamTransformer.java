package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A transformer for each item of a stream
 *
 * @author HAM
 *
 * @param <DTO> the type of DTO
 * @param <Entity> the type of Entity
 */
public interface StreamTransformer<DTO, Entity>
{

    /**
     * Transforms the entities in the {@link Stream} to DTOs and returns the {@link Stream} with DTOs. Ignores entities
     * that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @return the streams iterator
     */
    Stream<DTO> toStream();

    /**
     * Transforms the stream to a {@link Collection} of DTOs. The {@link Collection} of DTOs will be created by the
     * specified factory. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param <DTOCollection> the type of the collection of DTOs
     * @param dtoCollectionFactory a factory for the needed collection
     * @return a collection
     */
    <DTOCollection extends Collection<DTO>> DTOCollection toCollection(Supplier<DTOCollection> dtoCollectionFactory);

    /**
     * Transforms the stream to a {@link HashSet} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @return a set
     */
    default Set<DTO> toHashSet()
    {
        return toCollection(HashSet::new);
    }

    /**
     * Transforms the stream to a {@link LinkedHashSet} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @return a set
     */
    default Set<DTO> toLinkedHashSet()
    {
        return toCollection(LinkedHashSet::new);
    }

    /**
     * Transforms the stream to a {@link SortedSet} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @return a set
     */
    default SortedSet<DTO> toTreeSet()
    {
        return toCollection(TreeSet::new);
    }

    /**
     * Transforms the stream to a {@link SortedSet} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param comparator the comparator for the tree set
     * @return a set
     */
    default SortedSet<DTO> toTreeSet(Comparator<? super DTO> comparator)
    {
        return toCollection(() -> new TreeSet<>(comparator));
    }

    /**
     * Transforms the stream to an {@link ArrayList} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @return a list
     */
    default List<DTO> toArrayList()
    {
        return toCollection(ArrayList::new);
    }

    /**
     * Transforms the stream to a {@link Map} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key will overwrite
     * each other. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param <Key> the type of the key
     * @param <DTOMap> the type of map
     * @param mapFactory a factory for the result map
     * @param keyFunction the function to extract the key from one entity
     * @return a map
     */
    <Key, DTOMap extends Map<Key, DTO>> DTOMap toMap(Supplier<DTOMap> mapFactory, Function<Entity, Key> keyFunction);

    /**
     * Transforms the stream to a {@link HashMap} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. This method does not group results. DTOs with the same key will overwrite
     * each other. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param <Key> the type of the key
     * @param keyFunction the function to extract the key from one entity
     * @return a map
     */
    default <Key> Map<Key, DTO> toHashMap(Function<Entity, Key> keyFunction)
    {
        return toMap(HashMap<Key, DTO>::new, keyFunction);
    }

    /**
     * Transforms the stream to a grouped {@link Map} of DTOs. Ignores entities that transform to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set.
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param <DTOCollection> the type of the collections in the result map
     * @param <DTOMap> the type of map
     * @param mapFactory a factory for the result map
     * @param groupKeyFunction extracts the key for the map
     * @param collectionFactory a factory for the collections in the result map
     * @return a map
     */
    <GroupKey, DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> toGroupedMap(
        Supplier<DTOMap> mapFactory, Function<Entity, GroupKey> groupKeyFunction,
        Supplier<DTOCollection> collectionFactory);

    /**
     * Transforms the stream to a grouped {@link Map} of {@link HashSet}s with DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param groupKeyFunction extracts the key for the map
     * @return a map
     */
    default <GroupKey> Map<GroupKey, Set<DTO>> toGroupedHashSets(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, Set<DTO>>::new, groupKeyFunction, HashSet::new);
    }

    /**
     * Transforms the stream to a grouped {@link Map} of {@link LinkedHashSet}s with DTOs. Ignores entities that
     * transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param groupKeyFunction extracts the key for the map
     * @return a map
     */
    default <GroupKey> Map<GroupKey, Set<DTO>> toGroupedLinkedHashSets(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, Set<DTO>>::new, groupKeyFunction, LinkedHashSet::new);
    }

    /**
     * Transforms the stream to a grouped {@link Map} of {@link TreeSet}s with DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param groupKeyFunction extracts the key for the map
     * @return a map
     */
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> toGroupedTreeSets(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction, TreeSet::new);
    }

    /**
     * Transforms the stream to a grouped {@link Map} of {@link TreeSet}s with DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param groupKeyFunction extracts the key for the map
     * @param comparator the comparator for the tree set
     * @return a map
     */
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> toGroupedTreeSets(Function<Entity, GroupKey> groupKeyFunction,
        Comparator<? super DTO> comparator)
    {
        return toGroupedMap(HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction, () -> new TreeSet<>(comparator));
    }

    /**
     * Transforms the stream to a grouped {@link Map} of {@link ArrayList}s with DTOs. Ignores entities that transform
     * to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <GroupKey> the type of the group key
     * @param groupKeyFunction extracts the key for the map
     * @return a map
     */
    default <GroupKey> Map<GroupKey, List<DTO>> toGroupedArrayLists(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, List<DTO>>::new, groupKeyFunction, ArrayList::new);
    }

}