package at.porscheinformatik.antimapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Implementation of the {@link GroupTransformer}.
 *
 * @author HAM
 *
 * @param <DTO> the type of DTO
 * @param <GroupKey> the type of the group key
 * @param <Entity> the type of Entity
 */
public abstract class AbstractGroupTransformer<DTO, GroupKey, Entity> implements GroupTransformer<DTO, GroupKey, Entity>
{

    private final Map<GroupKey, ? extends Iterable<? extends Entity>> entities;
    private final Object[] hints;

    protected AbstractGroupTransformer(Map<GroupKey, ? extends Iterable<? extends Entity>> entities, Object... hints)
    {
        super();

        this.entities = entities;
        this.hints = hints;
    }

    protected abstract <DTOCollection extends Collection<DTO>> DTOCollection transformAll(
        Iterable<? extends Entity> values, Supplier<DTOCollection> collectionFactory, Object[] hints);

    protected abstract Object[] getTransformerHints();

    protected boolean containsHint(Object object)
    {
        return Hints.containsHint(hints, object) || Hints.containsHint(getTransformerHints(), object);
    }

    @Override
    public <DTOCollection extends Collection<DTO>, DTOMap extends Map<GroupKey, DTOCollection>> Map<GroupKey, DTOCollection> toGroupedMap(
        Supplier<DTOMap> mapFactory, Supplier<DTOCollection> collectionFactory)
    {
        Map<GroupKey, ? extends Iterable<? extends Entity>> entities = this.entities;

        if (entities == null)
        {
            if (!containsHint(Hint.OR_EMPTY))
            {
                return null;
            }

            entities = Collections.emptyMap();
        }

        try
        {
            DTOMap dtos = mapFactory.get();

            for (Entry<GroupKey, ? extends Iterable<? extends Entity>> entry : entities.entrySet())
            {
                dtos.put(entry.getKey(),
                    transformAll(entry.getValue(), collectionFactory, Hints.join(hints, entry.getKey())));
            }

            if (containsHint(Hint.UNMODIFIABLE))
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

}
