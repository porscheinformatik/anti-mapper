package at.porscheinformatik.antimapper.sample.parentchild;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import at.porscheinformatik.antimapper.Hints;
import at.porscheinformatik.antimapper.Mapper;

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

        ParentDTO dto = new ParentDTO(entity.getId());

        // add the DTO to the hints, just in case the child mapper needs it
        hints = Hints.join(hints, dto);

        dto.setName(entity.getKey());

        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        dto.setTimestamp(entity.getTimestamp().toInstant().atZone(timezone).toLocalDateTime());

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

        // add the entity to the hints, just in case the child mapper needs it
        hints = Hints.join(hints, entity);

        entity.setKey(dto.getName());

        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        entity.setTimestamp(Date.from(dto.getTimestamp().atZone(timezone).toInstant()));

        entity.setChilds(childMapper.mergeGroupedMapIntoTreeSet(dto.getChilds(), entity.getChilds(), hints));

        return entity;
    }

    @Override
    public boolean isUniqueKeyMatching(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

}
