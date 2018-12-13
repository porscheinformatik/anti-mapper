package at.porscheinformatik.antimapper;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
        return transformAll(() -> MapperUtils.streamOrNull(entities), hints);
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
        return transformAll(() -> entityStream, hints);
    }

    /**
     * Creates a {@link StreamTransformer} to transform each item in the {@link Stream}. Ignores entities that transform
     * to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an unmodifiable instance if the
     * {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is set.
     *
     * @param entityStreamSupplier the entities
     * @param hints optional hints
     * @return a {@link StreamTransformer}
     * @deprecated this should be private
     */
    @Deprecated
    default StreamTransformer<DTO, Entity> transformAll(Supplier<Stream<? extends Entity>> entityStreamSupplier,
        Object... hints)
    {
        return new AbstractStreamTransformer<DTO, Entity, Entity>(entityStreamSupplier, hints)
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
     * @param entities the entities
     * @param hints optional hints
     * @return a {@link StreamTransformer}
     */
    default StreamTransformer<DTO, Entity> transformAll(Map<?, ? extends Entity> entities, Object... hints)
    {
        return new AbstractStreamTransformer<DTO, Entity, Entry<?, ? extends Entity>>(
            () -> entities != null ? entities.entrySet().stream() : null, hints)
        {
            @Override
            protected DTO transform(Entry<?, ? extends Entity> container, Object[] hints)
            {
                return Transformer.this.transform(container.getValue(), Hints.join(hints, container.getKey()));
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Entry<?, ? extends Entity> container)
            {
                return keyFunction.apply(container.getValue());
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
     * @param <GroupKey> the type of the group key
     * @param entityPairStreamSupplier the entities
     * @param hints optional hints
     * @return a {@link StreamTransformer}
     * @deprecated this should be private
     */
    @Deprecated
    default <GroupKey> PairTransformer<DTO, GroupKey, Entity> transformAllPairs(
        Supplier<Stream<? extends Pair<? extends GroupKey, ? extends Entity>>> entityPairStreamSupplier,
        Object... hints)
    {
        return new AbstractPairTransformer<DTO, GroupKey, Entity>(entityPairStreamSupplier, hints)
        {
            @Override
            protected DTO transform(Pair<? extends GroupKey, ? extends Entity> container, Object[] hints)
            {
                return Transformer.this.transform(Pair.rightOf(container), Hints.join(hints, Pair.leftOf(container)));
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction,
                Pair<? extends GroupKey, ? extends Entity> container)
            {
                return keyFunction.apply(Pair.rightOf(container));
            }

            @Override
            protected Object[] getTransformerHints()
            {
                throw new UnsupportedOperationException("Method \"getTransformerHints(..)\" not implemented");
            }
        };
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param entities the parents iterator, may be null
     * @param hints the hints
     * @return the {@link StreamTransformer}
     */
    default StreamTransformer<DTO, Entity> flatMapAndTransformAll(Map<?, ? extends Iterable<? extends Entity>> entities,
        Object... hints)
    {
        return flatMapAndTransformAll(() -> entities != null ? entities.entrySet().stream() : null,
            entry -> MapperUtils.streamOrNull(entry.getValue()), hints);
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param <ParentEntity> the type of parent
     * @param parents the parents iterator, may be null
     * @param mapper the mapper
     * @param hints the hints
     * @return the {@link StreamTransformer}
     */
    default <ParentEntity> StreamTransformer<DTO, Entity> flatMapAndTransformAll(
        Iterable<? extends ParentEntity> parents,
        Function<? super ParentEntity, ? extends Stream<? extends Entity>> mapper, Object... hints)
    {
        return flatMapAndTransformAll(() -> MapperUtils.streamOrNull(parents), mapper, hints);
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param <ParentEntity> the type of parent
     * @param parents the parents stream, may be null
     * @param mapper the mapper
     * @param hints the hints
     * @return the {@link StreamTransformer}
     */
    default <ParentEntity> StreamTransformer<DTO, Entity> flatMapAndTransformAll(Stream<? extends ParentEntity> parents,
        Function<? super ParentEntity, ? extends Stream<? extends Entity>> mapper, Object... hints)
    {
        return flatMapAndTransformAll(() -> parents, mapper, hints);
    }

    /**
     * Create a {@link StreamTransformer} that creates the list of entities from parents. You can use this to flatten
     * maps. Ignores entities that transform to null, unless the {@link Hint#KEEP_NULL} hint is set. Returns an
     * unmodifiable instance if the {@link Hint#UNMODIFIABLE} is set. Never returns null if the {@link Hint#OR_EMPTY} is
     * set.
     *
     * @param <ParentEntity> the type of parent
     * @param parentsStreamSupplier the supplier for the parents stream
     * @param mapper the mapper
     * @param hints the hints
     * @return the {@link StreamTransformer}
     * @deprecated this should be private
     */
    @Deprecated
    default <ParentEntity> StreamTransformer<DTO, Entity> flatMapAndTransformAll(
        Supplier<Stream<? extends ParentEntity>> parentsStreamSupplier,
        Function<? super ParentEntity, ? extends Stream<? extends Entity>> mapper, Object... hints)
    {
        // This awfully complex line flattens the parents by using the mapper
        // and returns a supplier for a stream with Entity/ParentEntity pairs.
        @SuppressWarnings("resource")
        Supplier<Stream<? extends Pair<ParentEntity, Entity>>> streamSupplier = () -> {
            Stream<? extends ParentEntity> parentStream = parentsStreamSupplier.get();

            if (parentStream == null)
            {
                return null;
            }

            return parentStream.filter(parentEntity -> parentEntity != null).flatMap(parentEntity -> {
                Stream<? extends Entity> stream = mapper.apply(parentEntity);

                return MapperUtils.streamOrEmpty(stream).map(entity -> Pair.of(parentEntity, entity));
            });
        };

        return new AbstractStreamTransformer<DTO, Entity, Pair<ParentEntity, Entity>>(streamSupplier, hints)
        {

            @Override
            protected DTO transform(Pair<ParentEntity, Entity> container, Object[] hints)
            {
                return Transformer.this.transform(Pair.rightOf(container), Hints.join(hints, Pair.leftOf(container)));
            }

            @Override
            protected <Key> Key toKey(Function<Entity, Key> keyFunction, Pair<ParentEntity, Entity> container)
            {
                return keyFunction.apply(Pair.rightOf(container));
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
    default <GroupKey> GroupTransformer<DTO, GroupKey, Entity> transformGrouped(
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
}
