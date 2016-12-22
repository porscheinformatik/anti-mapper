package at.porscheinformatik.antimapper.sample.parentchild;

import java.util.Collection;
import java.util.Objects;

import at.porscheinformatik.antimapper.AbstractMapper;
import at.porscheinformatik.antimapper.Hints;

public class ChildMapper extends AbstractMapper<ChildDTO, ChildEntity>
{

    @Override
    protected ChildDTO transformNonNull(ChildEntity entity, Object[] hints)
    {
        return new ChildDTO(entity.getId(), entity.getKey(), entity.getType().name());
    }

    @Override
    protected ChildEntity mergeNonNull(ChildDTO dto, ChildEntity entity, Object[] hints)
    {
        entity.setKey(dto.getName());
        entity.setType(ChildType.valueOf(dto.getType()));

        // the parent will be taken from the hints. The ParentMapper makes sure it is available.
        entity.setParent(Hints.hint(hints, ParentEntity.class));

        return entity;
    }

    @Override
    protected ChildEntity create(ChildDTO dto, Object[] hints)
    {
        return new ChildEntity(dto.getId());
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
