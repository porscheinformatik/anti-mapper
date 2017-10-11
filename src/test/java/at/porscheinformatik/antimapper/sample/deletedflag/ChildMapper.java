package at.porscheinformatik.antimapper.sample.deletedflag;

import java.util.Collection;
import java.util.Objects;

import at.porscheinformatik.antimapper.AbstractMapper;
import at.porscheinformatik.antimapper.Hints;

public class ChildMapper extends AbstractMapper<ChildDTO, ChildEntity>
{

    @Override
    protected ChildDTO transformNonNull(ChildEntity entity, Object[] hints)
    {
        // a deleted entity will not be transformed
        if (entity.isDeleted())
        {
            return null;
        }

        return new ChildDTO(entity.getId(), entity.getName(), entity.getType().name());
    }

    @Override
    protected ChildEntity mergeNull(ChildEntity entity, Object[] hints)
    {
        // the entity will be deleted if the DTO is missing
        entity.setDeleted(true);

        return entity;
    }

    @Override
    protected ChildEntity mergeNonNull(ChildDTO dto, ChildEntity entity, Object[] hints)
    {
        entity.setName(dto.getName());

        // the parent will be taken from the hints. The ParentMapper makes sure it is available.
        entity.setParent(Hints.hint(hints, ParentEntity.class));

        // ensure, that the entity is not deleted, because there was a DTO
        entity.setDeleted(false);

        return entity;
    }

    @Override
    protected ChildEntity create(ChildDTO dto, Object[] hints)
    {
        ChildEntity entity = new ChildEntity();

        entity.setId(dto.getId());

        return entity;
    }

    @Override
    public void afterMergeIntoCollection(Collection<ChildEntity> entities, Object... hints)
    {
        int ordinal = 0;

        for (ChildEntity entity : entities)
        {
            ordinal = Math.max(ordinal, entity.getOrdinal());

            entity.setOrdinal(ordinal++);
        }
    }

    @Override
    public boolean isUniqueKeyMatching(ChildDTO dto, ChildEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

}
