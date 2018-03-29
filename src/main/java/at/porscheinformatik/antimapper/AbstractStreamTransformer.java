package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
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

    private final Supplier<Stream<? extends EntityContainer>> streamSupplier;
    private final Object[] hints;

    public AbstractStreamTransformer(Supplier<Stream<? extends EntityContainer>> streamSupplier, Object... hints)
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
    public Set<DTO> toHashSet()
    {
        return toCollection(HashSet::new);
    }

    @Override
    public SortedSet<DTO> toTreeSet()
    {
        return toCollection(TreeSet::new);
    }

    @Override
    public SortedSet<DTO> toTreeSet(Comparator<? super DTO> comparator)
    {
        return toCollection(() -> new TreeSet<>(comparator));
    }

    @Override
    public List<DTO> toArrayList()
    {
        return toCollection(ArrayList::new);
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
    public <Key> Map<Key, DTO> toHashMap(Function<Entity, Key> keyFunction)
    {
        return toMap(HashMap<Key, DTO>::new, keyFunction);
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
            Map<GroupKey, DTOCollection> dtos =
                MapperUtils.mapMixedGroups(stream, mapFactory.get(), entity -> toKey(groupKeyFunction, entity),
                    collectionFactory, (entity, dto) -> false, (entity, dto) -> transform(entity, hints),
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

    @Override
    public <GroupKey> Map<GroupKey, Set<DTO>> toGroupedHashSets(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, Set<DTO>>::new, groupKeyFunction, HashSet::new);
    }

    @Override
    public <GroupKey> Map<GroupKey, SortedSet<DTO>> toGroupedTreeSets(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction, TreeSet::new);
    }

    @Override
    public <GroupKey> Map<GroupKey, SortedSet<DTO>> toGroupedTreeSets(Function<Entity, GroupKey> groupKeyFunction,
        Comparator<? super DTO> comparator)
    {
        return toGroupedMap(HashMap<GroupKey, SortedSet<DTO>>::new, groupKeyFunction, () -> new TreeSet<>(comparator));
    }

    @Override
    public <GroupKey> Map<GroupKey, List<DTO>> toGroupedArrayLists(Function<Entity, GroupKey> groupKeyFunction)
    {
        return toGroupedMap(HashMap<GroupKey, List<DTO>>::new, groupKeyFunction, ArrayList::new);
    }

}
