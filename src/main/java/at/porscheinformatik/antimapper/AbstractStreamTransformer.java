package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A transformer for a given stream.
 *
 * @author HAM
 * @param <DTO> the type of DTO
 * @param <Entity> the type of Entity
 * @param <EntityContainer> the container for the Entity holding additional information
 */
public abstract class AbstractStreamTransformer<DTO, Entity, EntityContainer> implements StreamTransformer<DTO, Entity>
{

    protected final Supplier<Stream<? extends EntityContainer>> streamSupplier;
    protected final Object[] hints;

    protected AbstractStreamTransformer(Supplier<Stream<? extends EntityContainer>> streamSupplier, Object... hints)
    {
        super();

        this.streamSupplier = streamSupplier;
        this.hints = hints;
    }

    protected abstract DTO transform(EntityContainer container, Object[] hints);

    protected abstract <Key> Key toKey(Function<Entity, Key> keyFunction, EntityContainer container);

    protected abstract Object[] getTransformerHints();

    protected boolean containsHint(Object object)
    {
        return Hints.containsHint(hints, object) || Hints.containsHint(getTransformerHints(), object);
    }

    @Override
    public Stream<DTO> toStream()
    {
        Stream<? extends EntityContainer> stream = streamSupplier.get();

        if (stream == null)
        {
            if (containsHint(Hint.OR_EMPTY))
            {
                return Stream.empty();
            }

            return null;
        }

        try
        {
            Stream<DTO> result = stream.map(dto -> transform(dto, hints));

            if (!containsHint(Hint.KEEP_NULL))
            {
                result = result.filter(dto -> dto != null);
            }

            return result;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities in stream: %s", e,
                MapperUtils.abbreviate(String.valueOf(stream), 4096));
        }
    }

    @Override
    public <DTOCollection extends Collection<DTO>> DTOCollection toCollection(
        Supplier<DTOCollection> dtoCollectionFactory)
    {
        Stream<? extends EntityContainer> stream = streamSupplier.get();

        if (stream == null)
        {
            if (!containsHint(Hint.OR_EMPTY))
            {
                return null;
            }

            stream = Stream.empty();
        }

        DTOCollection dtos = toStream().collect(Collectors.toCollection(dtoCollectionFactory));

        if (containsHint(Hint.UNMODIFIABLE))
        {
            dtos = MapperUtils.toUnmodifiableCollection(dtos);
        }

        return dtos;

    }

    @Override
    public <Key, DTOMap extends Map<Key, DTO>> DTOMap toMap(Supplier<DTOMap> mapFactory,
        Function<Entity, Key> keyFunction)
    {
        Stream<? extends EntityContainer> stream = streamSupplier.get();

        if (stream == null)
        {
            if (!containsHint(Hint.OR_EMPTY))
            {
                return null;
            }

            stream = Stream.empty();
        }

        boolean keepNull = containsHint(Hint.KEEP_NULL);

        try
        {
            DTOMap dtos = mapFactory.get();

            stream.forEach(entity -> {
                if (entity == null)
                {
                    return;
                }

                Key key = toKey(keyFunction, entity);
                DTO dto = transform(entity, hints);

                if (dto != null || keepNull)
                {
                    dtos.put(key, dto);
                }
            });

            if (containsHint(Hint.UNMODIFIABLE))
            {
                return MapperUtils.toUnmodifiableMap(dtos);
            }

            return dtos;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a map: %s", e,
                MapperUtils.abbreviate(String.valueOf(stream), 4096));
        }
    }

    @Override
    public <GroupKey, DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> toGroupedMap(
        Supplier<DTOMap> mapFactory, Function<Entity, GroupKey> groupKeyFunction,
        Supplier<DTOCollection> collectionFactory)
    {
        Stream<? extends EntityContainer> stream = streamSupplier.get();

        if (stream == null)
        {
            if (!containsHint(Hint.OR_EMPTY))
            {
                return null;
            }

            stream = Stream.empty();
        }

        try
        {
            Map<GroupKey, DTOCollection> dtos = MapperUtils
                .mapMixedGroups(stream, mapFactory.get(), entity -> toKey(groupKeyFunction, entity), collectionFactory,
                    (entity, dto) -> false, (entity, dto) -> transform(entity, hints), false,
                    containsHint(Hint.KEEP_NULL) ? null : dto -> dto != null, map -> {
                        if (containsHint(Hint.UNMODIFIABLE))
                        {
                            List<GroupKey> keys = new ArrayList<>(map.keySet());

                            keys.forEach(key -> map.put(key, MapperUtils.toUnmodifiableCollection(map.get(key))));
                        }
                    });

            if (containsHint(Hint.UNMODIFIABLE))
            {
                dtos = MapperUtils.toUnmodifiableMap(dtos);
            }

            return dtos;
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a grouped map: %s", e,
                MapperUtils.abbreviate(String.valueOf(stream), 4096));
        }
    }

}
