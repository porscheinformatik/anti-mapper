package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
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
public interface Transformer<DTO, Entity> extends HintsProvider
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
     * Transforms the entity to a DTO. An empty optional will be handled as null.
     *
     * @param entity the entity, may be null
     * @param hints optional hints
     * @return the DTO, may be null if the entity is null
     */
    default DTO transform(Optional<? extends Entity> entity, Object... hints)
    {
        return transform(entity.orElse(null), hints);
    }

    /**
     * Creates a {@link StreamTransformer} to transform each item in the {@link Iterable}. Ignores entities that
     * transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a {@link StreamTransformer}
     */
    default StreamTransformer<DTO, Entity> transformAll(Iterable<? extends Entity> entities, Object... hints)
    {
        return new AbstractStreamTransformer<DTO, Entity, Entity>(
            () -> entities != null ? StreamSupport.stream(entities.spliterator(), false) : null, hints)
        {
            @Override
            protected DTO transform(Entity container, Object[] hints)
            {
                return Transformer.this.transform(container, hints);
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Entity container)
            {
                return keyFunction.apply(container);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return getDefaultHints();
            }
        };
    }

    /**
     * Creates a {@link StreamTransformer} to transform each item in the {@link Stream}. Ignores entities that transform
     * to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entityStream the entities, may be null
     * @param hints optional hints
     * @return a {@link StreamTransformer}
     */
    default StreamTransformer<DTO, Entity> transformAll(Stream<? extends Entity> entityStream, Object... hints)
    {
        return new AbstractStreamTransformer<DTO, Entity, Entity>(() -> entityStream, hints)
        {
            @Override
            protected DTO transform(Entity container, Object[] hints)
            {
                return Transformer.this.transform(container, hints);
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Entity container)
            {
                return keyFunction.apply(container);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return getDefaultHints();
            }
        };
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param <ParentEntity> the type of parent
     * @param parents the parents
     * @param mapper the mapper
     * @param hints the hints
     * @return the {@link StreamTransformer}
     */
    default <ParentEntity> StreamTransformer<DTO, Entity> flatMapAndTransformAll(
        Iterable<? extends ParentEntity> parents,
        Function<? super ParentEntity, ? extends Iterable<? extends Entity>> mapper, Object... hints)
    {
        // This awfully complex line flattens the parents by using the mapper
        // and returns a supplier for a stream with Entity/ParentEntity pairs.
        Supplier<Stream<? extends Pair<Entity, ParentEntity>>> streamSupplier = parents == null ? () -> Stream.empty()
            : () -> StreamSupport
                .stream(parents.spliterator(), false)
                .filter(parentEntity -> parentEntity != null)
                .flatMap(parentEntity -> {
                    Iterable<? extends Entity> iterable = mapper.apply(parentEntity);
                    Stream<? extends Entity> stream =
                        iterable == null ? Stream.empty() : StreamSupport.stream(iterable.spliterator(), false);

                    return stream.map(entity -> Pair.of(entity, parentEntity));
                });

        return new AbstractStreamTransformer<DTO, Entity, Pair<Entity, ParentEntity>>(streamSupplier, hints)
        {
            @Override
            protected DTO transform(Pair<Entity, ParentEntity> container, Object[] hints)
            {
                Entity entity = container.getLeft();
                ParentEntity parentEntity = container.getRight();

                return Transformer.this.transform(entity, Hints.join(hints, parentEntity));
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Pair<Entity, ParentEntity> container)
            {
                Entity entity = container.getLeft();

                return keyFunction.apply(entity);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return getDefaultHints();
            }
        };
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param <ParentEntity> the type of parent
     * @param parents the parents
     * @param mapper the mapper
     * @param hints the hints
     * @return the {@link StreamTransformer}
     */
    default <ParentEntity> StreamTransformer<DTO, Entity> flatMapAndTransformAll(Stream<? extends ParentEntity> parents,
        Function<? super ParentEntity, ? extends Iterable<? extends Entity>> mapper, Object... hints)
    {
        // This awfully complex line flattens the parents by using the mapper
        // and returns a supplier for a stream with Entity/ParentEntity pairs.
        Supplier<Stream<? extends Pair<Entity, ParentEntity>>> streamSupplier = parents == null ? () -> Stream.empty()
            : () -> parents.filter(parentEntity -> parentEntity != null).flatMap(parentEntity -> {
                Iterable<? extends Entity> iterable = mapper.apply(parentEntity);
                Stream<? extends Entity> stream =
                    iterable == null ? Stream.empty() : StreamSupport.stream(iterable.spliterator(), false);

                return stream.map(entity -> Pair.of(entity, parentEntity));
            });

        return new AbstractStreamTransformer<DTO, Entity, Pair<Entity, ParentEntity>>(streamSupplier, hints)
        {
            @Override
            protected DTO transform(Pair<Entity, ParentEntity> container, Object[] hints)
            {
                Entity entity = container.getLeft();
                ParentEntity parentEntity = container.getRight();

                return Transformer.this.transform(entity, Hints.join(hints, parentEntity));
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Pair<Entity, ParentEntity> container)
            {
                Entity entity = container.getLeft();

                return keyFunction.apply(entity);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return getDefaultHints();
            }
        };
    }

    /**
     * Transforms all items of a grouped map. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL}
     * hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set. The key entities map key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return the {@link GroupTransformer}
     */
    default <GroupKey> GroupTransformer<DTO, GroupKey, Entity> transformAllGrouped(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Object... hints)
    {
        return new AbstractGroupTransformer<DTO, GroupKey, Entity>(entities, hints)
        {
            @Override
            protected <DTOCollection extends Collection<DTO>> DTOCollection transformAll(
                Iterable<? extends Entity> values, Supplier<DTOCollection> collectionFactory, Object[] hints)
            {
                return Transformer.this.transformAll(values, hints).toCollection(collectionFactory);
            }

            @Override
            protected Object[] getTransformerHints()
            {
                return getDefaultHints();
            }
        };
    }

    /**
     * Transforms the entities in the {@link Stream} to DTOs and returns the {@link Stream} with DTOs. Ignores entities
     * that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     * @deprecated use the {@link #transformAll(Stream, Object...)} interface
     */
    @Deprecated
    default Stream<DTO> transformEach(Stream<? extends Entity> entities, Object... hints)
    {
        return transformAll(entities, hints).toStream();
    }

    /**
     * Transforms the entities in the {@link Collection} to DTOs and returns the {@link Stream} with DTOs. Removes
     * entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the stream, may be null
     * @param hints optional hints
     * @return the streams iterator
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default Stream<DTO> transformToStream(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformAll(entities, hints).toStream();
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <DTOCollection extends Collection<DTO>> DTOCollection transformToCollection(
        Iterable<? extends Entity> entities, Supplier<DTOCollection> dtoCollectionFactory, Object... hints)
    {
        return transformAll(entities, hints).toCollection(dtoCollectionFactory);
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link HashSet} of DTOs. Ignores entities that transform to null,
     * unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE}
     * is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default Set<DTO> transformToHashSet(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformAll(entities, hints).toHashSet();
    }

    /**
     * Transforms an {@link Iterable} of entities to a {@link SortedSet} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a set
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default SortedSet<DTO> transformToTreeSet(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformAll(entities, hints).toTreeSet();
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default SortedSet<DTO> transformToTreeSet(Iterable<? extends Entity> entities, Comparator<? super DTO> comparator,
        Object... hints)
    {
        return transformAll(entities, hints).toTreeSet(comparator);
    }

    /**
     * Transforms an {@link Iterable} of entities to an {@link ArrayList} of DTOs. Ignores entities that transform to
     * null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a list
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default List<DTO> transformToArrayList(Iterable<? extends Entity> entities, Object... hints)
    {
        return transformAll(entities, hints).toArrayList();
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <Key, DTOMap extends Map<Key, DTO>> DTOMap transformToMap(Iterable<? extends Entity> entities,
        Supplier<DTOMap> mapFactory, Function<Entity, Key> keyFunction, Object... hints)
    {
        return transformAll(entities, hints).toMap(mapFactory, keyFunction);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <Key> Map<Key, DTO> transformToHashMap(Iterable<? extends Entity> entities,
        Function<Entity, Key> keyFunction, Object... hints)
    {
        return transformAll(entities, hints).toHashMap(keyFunction);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <GroupKey, DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> transformToGroupedMap(
        Iterable<? extends Entity> entities, Supplier<DTOMap> mapFactory, Function<Entity, GroupKey> groupKeyFunction,
        Supplier<DTOCollection> collectionFactory, Object... hints)
    {
        return transformAll(entities, hints).toGroupedMap(mapFactory, groupKeyFunction, collectionFactory);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, Set<DTO>> transformToGroupedHashSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformAll(entities, hints).toGroupedHashSets(groupKeyFunction);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformToGroupedTreeSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformAll(entities, hints).toGroupedTreeSets(groupKeyFunction);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformToGroupedTreeSets(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Comparator<? super DTO> comparator, Object... hints)
    {
        return transformAll(entities, hints).toGroupedTreeSets(groupKeyFunction, comparator);
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
     * @deprecated use the {@link #transformAll(Iterable, Object...)} interface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, List<DTO>> transformToGroupedArrayLists(Iterable<? extends Entity> entities,
        Function<Entity, GroupKey> groupKeyFunction, Object... hints)
    {
        return transformAll(entities, hints).toGroupedArrayLists(groupKeyFunction);
    }

    /**
     * Transforms a grouped map with an {@link Iterable} of entities to a grouped {@link Map} of DTOs. Ignores entities
     * that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set. The key entities map
     * key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param <DTOCollection> the type of the collections in the result map
     * @param <DTOMap> the type of map
     * @param entities the entities, may be null
     * @param mapFactory a factory for the result map
     * @param collectionFactory a factory for the collections in the result map
     * @param hints optional hints
     * @return a map
     * @deprecated use the {@link #transformAllGrouped(Map, Object...)} iterface
     */
    @Deprecated
    default <GroupKey, DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> transformGroupedMapToGroupedMap(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Supplier<DTOMap> mapFactory,
        Supplier<DTOCollection> collectionFactory, Object... hints)
    {
        return transformAllGrouped(entities, hints).toGroupedMap(mapFactory, collectionFactory);
    }

    /**
     * Transforms a grouped map with an {@link Iterable} of entities to a grouped {@link HashMap} with an
     * {@link ArrayList} of DTOs. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is
     * set. Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set. The key entities map key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     * @deprecated use the {@link #transformAllGrouped(Map, Object...)} iterface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, List<DTO>> transformGroupedMapToGroupedArrayLists(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Object... hints)
    {
        return transformAllGrouped(entities, hints).toGroupedArrayLists();
    }

    /**
     * Transforms a grouped map with an {@link Iterable} of entities to a grouped {@link HashMap} with an
     * {@link HashSet} of DTOs. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set. The key entities map key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     * @deprecated use the {@link #transformAllGrouped(Map, Object...)} iterface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, Set<DTO>> transformGroupedMapToGroupedHashSets(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Object... hints)
    {
        return transformAllGrouped(entities, hints).toGroupedHashSets();
    }

    /**
     * Transforms a grouped map with an {@link Iterable} of entities to a grouped {@link HashMap} with an
     * {@link TreeSet} of DTOs. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set. The key entities map key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param hints optional hints
     * @return a map
     * @deprecated use the {@link #transformAllGrouped(Map, Object...)} iterface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformGroupedMapToGroupedTreeSets(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Object... hints)
    {
        return transformAllGrouped(entities, hints).toGroupedTreeSets();
    }

    /**
     * Transforms a grouped map with an {@link Iterable} of entities to a grouped {@link HashMap} with an
     * {@link TreeSet} of DTOs. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set.
     * Returns an unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the
     * {@link Hint#OR_EMPTY} is set. The key entities map key is added to the hints for each transformation round.
     *
     * @param <GroupKey> the type of the group key
     * @param entities the entities, may be null
     * @param comparator the comparator for the tree set
     * @param hints optional hints
     * @return a map
     * @deprecated use the {@link #transformAllGrouped(Map, Object...)} iterface
     */
    @Deprecated
    default <GroupKey> Map<GroupKey, SortedSet<DTO>> transformGroupedMapToGroupedTreeSets(
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Comparator<? super DTO> comparator,
        Object... hints)
    {
        return transformAllGrouped(entities, hints).toGroupedTreeSets(comparator);
    }

}
