package at.porscheinformatik.antimapper;

import java.util.Objects;
import java.util.function.Function;

/**
 * Function to check if the DTO and the Entity is matching.
 *
 * @author ham
 * @param <DTO> the type of the DTO
 * @param <Entity> the type of the entity
 */
@FunctionalInterface
public interface UniqueKeyMatchingFunction<DTO, Entity>
{

    static <DTO, Entity> UniqueKeyMatchingFunction<DTO, Entity> unsupported()
    {
        return (dto, entity, hints) -> {
            throw new UnsupportedOperationException("UniqueKeyMatching not supported");
        };
    }

    static <DTO, Entity> UniqueKeyMatchingFunction<DTO, Entity> equalityOf(Function<DTO, Object> dtoGetter,
        Function<Entity, Object> entityGetter)
    {
        return (dto, entity, hints) -> Objects.equals(dtoGetter.apply(dto), entityGetter.apply(entity));
    }

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

}