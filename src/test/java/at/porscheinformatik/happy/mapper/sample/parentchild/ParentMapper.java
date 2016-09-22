package at.porscheinformatik.happy.mapper.sample.parentchild;

import java.util.Objects;

import at.porscheinformatik.happy.mapper.Hints;
import at.porscheinformatik.happy.mapper.Mapper;

public class ParentMapper implements Mapper<ParentDTO, ParentEntity>
{

    private final ChildMapper childMapper;

    public ParentMapper(ChildMapper childMapper)
    {
        super();
        this.childMapper = childMapper;
    }

    @Override
    public ParentDTO transform(ParentEntity entity, Object... hints)
    {
        // the null-check is mandatory
        if (entity == null)
        {
            return null;
        }

        ParentDTO dto = new ParentDTO();

        // add the entity and the DTO to the hints, just in case the childMapper needs it
        hints = Hints.join(hints, entity, dto);

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setChilds(childMapper.transformToGroupedArrayLists(entity.getChilds(), child -> child.getType(), hints));

        return dto;
    }

    @Override
    public ParentEntity merge(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        // the null-check is mandatory
        if (dto == null)
        {
            return null;
        }

        // if the ids do not match, create a new instance (Hibernate will be very thankful for this)
        if ((entity == null) || (!Objects.equals(dto.getId(), entity.getId())))
        {
            entity = new ParentEntity();

            entity.setId(dto.getId());
        }

        // add the entity and the DTO to the hints, just in case the childMapper needs it
        hints = Hints.join(hints, entity, dto);

        entity.setName(dto.getName());
        entity.setChilds(childMapper.mergeGroupedMapIntoTreeSet(dto.getChilds(), entity.getChilds(), hints));

        return entity;
    }

    @Override
    public boolean isUniqueKeyMatching(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

}
