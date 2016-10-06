package at.porscheinformatik.happy.mapper.sample.parentchild;

import java.util.Collection;
import java.util.Objects;

import at.porscheinformatik.happy.mapper.Hints;
import at.porscheinformatik.happy.mapper.Mapper;

public class ChildMapper implements Mapper<ChildDTO, ChildEntity>
{

    @Override
    public ChildDTO transform(ChildEntity entity, Object... hints)
    {
        // the null-check is mandatory
        if (entity == null)
        {
            return null;
        }

        return new ChildDTO(entity.getId(), entity.getKey(), entity.getType().name());
    }

    @Override
    public ChildEntity merge(ChildDTO dto, ChildEntity entity, Object... hints)
    {
        // the null-check is mandatory
        if (dto == null)
        {
            return null;
        }

        // if the ids do not match, create a new instance (Hibernate will be very thankful for this)
        if ((entity == null) || (!Objects.equals(dto.getId(), entity.getId())))
        {
            entity = new ChildEntity();

            entity.setId(dto.getId());
        }

        entity.setKey(dto.getName());
        entity.setType(ChildType.valueOf(dto.getType()));

        // the parent will be taken from the hints. The ParentMapper makes sure it is available.
        entity.setParent(Hints.hint(hints, ParentEntity.class));

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
