package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A merger is a mapper that merges a DTO into an entity.
 *
 * @author ham
 * @param <DTO> the dto type
 * @param <Entity> the entity type
 */
public interface Merger<DTO, Entity> extends HintsProvider
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
    Entity merge(DTO dto, Entity entity, Object... hints);

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
    boolean isUniqueKeyMatching(DTO dto, Entity entity, Object... hints);

    default boolean isUniqueKeyMatchingNullable(DTO dto, Entity entity, Object... hints)
    {
        if (dto == entity)
        {
            return true;
        }

        if (dto == null || entity == null)
        {
            return false;
        }

        return isUniqueKeyMatching(dto, entity, hints);
    }

    /**
     * Creates a {@link StreamMerger} for merging multiple DTOs into multiple Entities. Ignores DTOs that merge to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set (always creates a new result object in this case, merging the entities). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default StreamMerger<DTO, Entity> mergeAll(Iterable<? extends DTO> dtos, Object... hints)
    {
        return mergeAll(() -> MapperUtils.streamOrNull(dtos), hints);
    }

    /**
     * Creates a {@link StreamMerger} for merging multiple DTOs into multiple Entities. Ignores DTOs that merge to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set (always creates a new result object in this case, merging the entities). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtoStream the DTOs, may be null
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default StreamMerger<DTO, Entity> mergeAll(Stream<? extends DTO> dtoStream, Object... hints)
    {
        return mergeAll(() -> dtoStream, hints);
    }

    /**
     * Creates a {@link StreamMerger} for merging multiple DTOs into multiple Entities. Ignores DTOs that merge to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set (always creates a new result object in this case, merging the entities). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtoStreamSupplier the supplier for the DTO stream
     * @param hints optional hints
     * @return a {@link StreamMerger}
     * @deprecated this should be private
     */
    @Deprecated
    default StreamMerger<DTO, Entity> mergeAll(Supplier<Stream<? extends DTO>> dtoStreamSupplier, Object... hints)
    {
        return new AbstractStreamMerger<DTO, DTO, Entity>(dtoStreamSupplier, hints)
        {
            @Override
            protected boolean isUniqueKeyMatchingNullable(DTO dto, Entity entity, Object[] hints)
            {
                return Merger.this.isUniqueKeyMatchingNullable(dto, entity, hints);
            }

            @Override
            protected Entity merge(DTO dto, Entity entity, Object[] hints)
            {
                return Merger.this.merge(dto, entity, hints);
            }

            @Override
            protected void afterMergeIntoCollection(Collection<Entity> entities, Object[] hints)
            {
                Merger.this.afterMergeIntoCollection(entities, hints);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return Merger.this.getDefaultHints();
            }
        };
    }

    /**
     * Creates a {@link StreamMerger} for merging multiple DTOs into multiple Entities. Ignores DTOs that merge to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set (always creates a new result object in this case, merging the entities). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the DTOs, may be null
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default StreamMerger<DTO, Entity> mergeAll(Map<?, ? extends DTO> dtos, Object... hints)
    {
        return new AbstractStreamMerger<DTO, Entry<?, ? extends DTO>, Entity>(
            () -> dtos != null ? dtos.entrySet().stream() : null, hints)
        {
            @Override
            protected boolean isUniqueKeyMatchingNullable(Entry<?, ? extends DTO> dtoContainer, Entity entity,
                Object[] hints)
            {
                if (dtoContainer == null)
                {
                    return Merger.this.isUniqueKeyMatchingNullable(null, entity, hints);
                }

                return Merger.this.isUniqueKeyMatchingNullable(dtoContainer.getValue(), entity, hints);
            }

            @Override
            protected Entity merge(Entry<?, ? extends DTO> dtoContainer, Entity entity, Object[] hints)
            {
                if (dtoContainer == null)
                {
                    return Merger.this.merge(null, entity, hints);
                }

                return Merger.this.merge(dtoContainer.getValue(), entity, Hints.join(hints, dtoContainer.getKey()));
            }

            @Override
            protected void afterMergeIntoCollection(Collection<Entity> entities, Object[] hints)
            {
                Merger.this.afterMergeIntoCollection(entities, hints);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return Merger.this.getDefaultHints();
            }
        };
    }

    /**
     * Flattens a map and merges each item. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is
     * set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object
     * in this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <ParentDTO> the type of parent DTO
     * @param dtos an iterable of parent DTOs, may be null
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default <ParentDTO> StreamMerger<DTO, Entity> flatMapAndMergeAll(Map<?, Iterable<? extends DTO>> dtos,
        Object... hints)
    {
        return flatMapAndMergeAll(() -> dtos != null ? dtos.entrySet().stream() : null, entry -> entry.getValue(),
            hints);
    }

    /**
     * Flattens an iterable of objects and merges them. Ignores DTOs that merge to null, unless the
     * {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set
     * (always creates a new result object in this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <ParentDTO> the type of parent DTO
     * @param parentDtoIterable an iterable of parent DTOs, may be null
     * @param mapper the mapper for flattening the parent
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default <ParentDTO> StreamMerger<DTO, Entity> flatMapAndMergeAll(Iterable<? extends ParentDTO> parentDtoIterable,
        Function<? super ParentDTO, ? extends Iterable<? extends DTO>> mapper, Object... hints)
    {
        return flatMapAndMergeAll(() -> MapperUtils.streamOrNull(parentDtoIterable), mapper, hints);
    }

    /**
     * Flattens a stream of objects and merges them. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL}
     * hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new
     * result object in this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <ParentDTO> the type of parent DTO
     * @param parentDtoStream the stream of parent DTOs, may be null
     * @param mapper the mapper for flattening the parent
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default <ParentDTO> StreamMerger<DTO, Entity> flatMapAndMergeAll(Stream<? extends ParentDTO> parentDtoStream,
        Function<? super ParentDTO, ? extends Iterable<? extends DTO>> mapper, Object... hints)
    {
        return flatMapAndMergeAll(() -> parentDtoStream, mapper, hints);
    }

    /**
     * Flattens a stream of objects and merges them. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL}
     * hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new
     * result object in this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <ParentDTO> the type of parent DTO
     * @param parentDtoStreamSupplier the suppliert for the parent DTO stream
     * @param mapper the mapper for flattening the parent
     * @param hints optional hints
     * @return a {@link StreamMerger}
     */
    default <ParentDTO> StreamMerger<DTO, Entity> flatMapAndMergeAll(
        Supplier<Stream<? extends ParentDTO>> parentDtoStreamSupplier,
        Function<? super ParentDTO, ? extends Iterable<? extends DTO>> mapper, Object... hints)
    {

        Supplier<Stream<? extends Pair<DTO, ParentDTO>>> dtoStreamSupplier = () -> {
            Stream<? extends ParentDTO> parentDtoStream = parentDtoStreamSupplier.get();

            if (parentDtoStream == null)
            {
                return null;
            }

            return parentDtoStream.filter(parentDto -> parentDto != null).flatMap(parentDto -> {
                Iterable<? extends DTO> iterable = mapper.apply(parentDto);
                Stream<? extends DTO> stream = MapperUtils.streamOrEmpty(iterable);

                return stream.map(dto -> Pair.of(dto, parentDto));
            });
        };

        return new AbstractStreamMerger<DTO, Pair<DTO, ParentDTO>, Entity>(dtoStreamSupplier, hints)
        {
            @Override
            protected boolean isUniqueKeyMatchingNullable(Pair<DTO, ParentDTO> dtoContainer, Entity entity,
                Object[] hints)
            {
                return Merger.this.isUniqueKeyMatchingNullable(dtoContainer.getLeft(), entity,
                    Hints.join(hints, dtoContainer.getRight()));
            }

            @Override
            protected Entity merge(Pair<DTO, ParentDTO> dtoContainer, Entity entity, Object[] hints)
            {
                return Merger.this.merge(dtoContainer.getLeft(), entity, Hints.join(hints, dtoContainer.getRight()));
            }

            @Override
            protected void afterMergeIntoCollection(Collection<Entity> entities, Object[] hints)
            {
                Merger.this.afterMergeIntoCollection(entities, hints);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return Merger.this.getDefaultHints();
            }
        };
    }

    /**
     * Merge the grouped DTOs
     *
     * @param <GroupKey> the type of the group key
     * @param dtos the DTOs, may be null or empty
     * @param hints some hints
     * @return the {@link GroupMerger}
     */
    default <GroupKey> GroupMerger<DTO, Entity> mergeGrouped(Map<GroupKey, ? extends Iterable<? extends DTO>> dtos,
        Object... hints)
    {
        return new AbstractGroupMerger<GroupKey, DTO, Entity>(dtos, hints)
        {
            @Override
            protected boolean isUniqueKeyMatchingNullable(DTO dto, Entity entity, Object[] hints)
            {
                return Merger.this.isUniqueKeyMatchingNullable(dto, entity, hints);
            }

            @Override
            protected Entity merge(DTO dto, Entity entity, Object[] hints)
            {
                return Merger.this.merge(dto, entity, hints);
            }

            @Override
            protected void afterMergeIntoCollection(Collection<Entity> entities, Object[] hints)
            {
                Merger.this.afterMergeIntoCollection(entities, hints);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return Merger.this.getDefaultHints();
            }
        };
    }

    /**
     * Maps a collection to a collection. Ignores the order. If the entities parameter is null, it creates a
     * {@link Collection} if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in
     * this case, merging the entities). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param dtos the DTOs, may be null
     * @param entities the entities, may be null
     * @param entityCollectionFactory a factory for the needed collection
     * @param hints optional hints
     * @return a collection
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeIntoMixedCollection(
        Iterable<? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoMixedCollection(entities, entityCollectionFactory);
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
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeIntoOrderedCollection(
        Iterable<? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoOrderedCollection(entities, entityCollectionFactory);
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
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default Set<Entity> mergeIntoHashSet(Iterable<? extends DTO> dtos, Set<Entity> entities, Object... hints)
    {
        return mergeAll(dtos, hints).intoHashSet(entities);
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
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeIntoTreeSet(Iterable<? extends DTO> dtos, SortedSet<Entity> entities,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoTreeSet(entities);
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
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeIntoTreeSet(Iterable<? extends DTO> dtos, SortedSet<Entity> entities,
        Comparator<? super Entity> comparator, Object... hints)
    {
        return mergeAll(dtos, hints).intoTreeSet(entities, comparator);
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
     * @deprecated use the {@link #mergeAll(Iterable, Object...)} interface
     */
    @Deprecated
    default List<Entity> mergeIntoArrayList(Iterable<? extends DTO> dtos, List<Entity> entities, Object... hints)
    {
        return mergeAll(dtos, hints).intoArrayList(entities);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoMixedCollection(
        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoMixedCollection(entities, entityCollectionFactory);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeMapIntoOrderedCollection(
        Map<?, ? extends DTO> dtos, EntityCollection entities, Supplier<EntityCollection> entityCollectionFactory,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoOrderedCollection(entities, entityCollectionFactory);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default Set<Entity> mergeMapIntoHashSet(Map<?, ? extends DTO> dtos, Set<Entity> entities, Object... hints)
    {
        return mergeAll(dtos, hints).intoHashSet(entities);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
        Object... hints)
    {
        return mergeAll(dtos, hints).intoTreeSet(entities);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeMapIntoTreeSet(Map<?, ? extends DTO> dtos, SortedSet<Entity> entities,
        Comparator<? super Entity> comparator, Object... hints)
    {
        return mergeAll(dtos, hints).intoTreeSet(entities, comparator);
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
     * @deprecated use the {@link #mergeAll(Map, Object...)} interface
     */
    @Deprecated
    default List<Entity> mergeMapIntoArrayList(Map<?, ? extends DTO> dtos, List<Entity> entities, Object... hints)
    {
        return mergeAll(dtos, hints).intoArrayList(entities);
    }

    /**
     * Maps a grouped map to a collection. Ignores the order. If the entities parameter is null, it creates a
     * {@link Collection} if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in
     * this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param entityCollectionFactory the factory for the entities collection
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeGroupedMapIntoMixedCollection(
        Map<?, ? extends Collection<? extends DTO>> dtos, EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoMixedCollection(entities, entityCollectionFactory);
    }

    /**
     * Maps a grouped map to a collection. Keeps the order. If the entities parameter is null, it creates a
     * {@link Collection} if necessary. Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in
     * this case). Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param <EntityCollection> the type of the collection
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param entityCollectionFactory the factory for the entities collection
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default <EntityCollection extends Collection<Entity>> EntityCollection mergeGroupedMapIntoOrderedCollection(
        Map<?, ? extends Collection<? extends DTO>> dtos, EntityCollection entities,
        Supplier<EntityCollection> entityCollectionFactory, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoOrderedCollection(entities, entityCollectionFactory);
    }

    /**
     * Maps a grouped map to a collection. If the entities parameter is null, it creates a {@link HashSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default Set<Entity> mergeGroupedMapIntoHashSet(Map<?, ? extends Collection<? extends DTO>> dtos,
        Set<Entity> entities, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoHashSet(entities);
    }

    /**
     * Maps a grouped map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeGroupedMapIntoTreeSet(Map<?, ? extends Collection<? extends DTO>> dtos,
        SortedSet<Entity> entities, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoTreeSet(entities);
    }

    /**
     * Maps a grouped map to a collection. If the entities parameter is null, it creates a {@link TreeSet} if necessary.
     * Ignores DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance
     * if the {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if
     * the {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param comparator the comparator
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default SortedSet<Entity> mergeGroupedMapIntoTreeSet(Map<?, ? extends Collection<? extends DTO>> dtos,
        SortedSet<Entity> entities, Comparator<? super Entity> comparator, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoTreeSet(entities, comparator);
    }

    /**
     * Maps a grouped map to a list. If the entities parameter is null, it creates a {@link List} if necessary. Ignores
     * DTOs that merge to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set (always creates a new result object in this case). Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param dtos the grouped dtos
     * @param entities the entities
     * @param hints optional hints
     * @return the collection
     * @deprecated use the {@link #mergeGrouped(Map, Object...)} interface
     */
    @Deprecated
    default List<Entity> mergeGroupedMapIntoArrayList(Map<?, ? extends Collection<? extends DTO>> dtos,
        List<Entity> entities, Object... hints)
    {
        return mergeGrouped(dtos, hints).intoArrayList(entities);
    }

}
