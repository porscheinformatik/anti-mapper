package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * A merger working on the items of a stream
 *
 * @author HAM
 *
 * @param <DTO> the type of DTO
 * @param <Entity> the type of Entity
 */
public interface StreamMerger<DTO, Entity>
{

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
    <EntityCollection extends Collection<Entity>> EntityCollection intoMixedCollection(EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory);

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
    <EntityCollection extends Collection<Entity>> EntityCollection intoOrderedCollection(EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory);

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @return a collection
     */
    default Set<Entity> intoHashSet(Set<Entity> entities)
    {
        return intoMixedCollection(entities, HashSet::new);
    }

    /**
     * Maps a collection to a collection. If the entities parameter is null, it creates a {@link LinkedHashSet} if
     * necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case).
     * Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @return a collection
     */
    default Set<Entity> intoLinkedHashSet(Set<Entity> entities)
    {
        return intoMixedCollection(entities, LinkedHashSet::new);
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
    default SortedSet<Entity> intoTreeSet(SortedSet<Entity> entities)
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
    default SortedSet<Entity> intoTreeSet(SortedSet<Entity> entities, Comparator<? super Entity> comparator)
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
    default List<Entity> intoArrayList(List<Entity> entities)
    {
        return intoOrderedCollection(entities, ArrayList::new);
    }

}