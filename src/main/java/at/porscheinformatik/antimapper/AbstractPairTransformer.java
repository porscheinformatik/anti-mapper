package at.porscheinformatik.antimapper;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Implementation of the {@link PairTransformer}.
 *
 * @author HAM
 *
 * @param <DTO> the type of DTO
 * @param <LeftEntity> the type of the key
 * @param <RightEntity> the type of Entity
 */
public abstract class AbstractPairTransformer<DTO, LeftEntity, RightEntity>
    extends AbstractStreamTransformer<DTO, RightEntity, Pair<? extends LeftEntity, ? extends RightEntity>>
    implements PairTransformer<DTO, LeftEntity, RightEntity>
{

    public AbstractPairTransformer(
        Supplier<Stream<? extends Pair<? extends LeftEntity, ? extends RightEntity>>> streamSupplier, Object... hints)
    {
        super(streamSupplier, hints);
    }

    @Override
    public <DTOCollection extends Collection<DTO>, DTOMap extends Map<LeftEntity, DTOCollection>> Map<LeftEntity, DTOCollection> toGroupedMap(
        Supplier<DTOMap> mapFactory, Supplier<DTOCollection> collectionFactory)
    {
        Stream<? extends Pair<? extends LeftEntity, ? extends RightEntity>> stream = streamSupplier.get();

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

            stream.forEach(entityContainer -> {
                DTO dto = transform(entityContainer, hints);

                if (dto == null && !keepNull)
                {
                    return;
                }

                LeftEntity groupKey = entityContainer.getLeft();
                DTOCollection dtoCollection = dtos.get(groupKey);

                if (dtoCollection == null)
                {
                    dtoCollection = collectionFactory.get();

                    dtos.put(groupKey, dtoCollection);
                }

                dtoCollection.add(dto);
            });

            if (!containsHint(Hint.UNMODIFIABLE))
            {
                return dtos;
            }

            DTOMap unmodifiableDtos = mapFactory.get();

            for (Entry<LeftEntity, DTOCollection> entry : dtos.entrySet())
            {
                unmodifiableDtos.put(entry.getKey(), MapperUtils.toUnmodifiableCollection(entry.getValue()));
            }

            return MapperUtils.toUnmodifiableMap(unmodifiableDtos);
        }
        catch (Exception e)
        {
            throw new MapperException("Failed to transform entities to a grouped map: %s", e,
                MapperUtils.abbreviate(String.valueOf(stream), 4096));
        }
    }

}
