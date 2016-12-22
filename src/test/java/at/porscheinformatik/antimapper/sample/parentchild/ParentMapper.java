package at.porscheinformatik.antimapper.sample.parentchild;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import at.porscheinformatik.antimapper.AbstractMapper;
import at.porscheinformatik.antimapper.Hints;

public class ParentMapper extends AbstractMapper<ParentDTO, ParentEntity>
{

    private final ChildMapper childMapper;

    public ParentMapper(ChildMapper childMapper)
    {
        super();

        this.childMapper = childMapper;
    }

    @Override
    protected ParentDTO transformNonNull(ParentEntity entity, Object[] hints)
    {
        ParentDTO dto = new ParentDTO(entity.getId());

        dto.setName(entity.getKey());

        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        dto.setTimestamp(entity.getTimestamp().toInstant().atZone(timezone).toLocalDateTime());

        dto.setChilds(childMapper.transformToGroupedArrayLists(entity.getChilds(), child -> child.getType(), hints));

        return dto;
    }

    @Override
    protected ParentEntity mergeNonNull(ParentDTO dto, ParentEntity entity, Object[] hints)
    {
        entity.setKey(dto.getName());

        ZoneId timezone = Hints.hint(hints, ZoneId.class);
        entity.setTimestamp(Date.from(dto.getTimestamp().atZone(timezone).toInstant()));

        entity.setChilds(childMapper.mergeGroupedMapIntoTreeSet(dto.getChilds(), entity.getChilds(), hints));

        return entity;
    }

    @Override
    protected ParentEntity create(ParentDTO dto, Object[] hints)
    {
        return new ParentEntity(dto.getId());
    }

    @Override
    public boolean isUniqueKeyMatching(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

}
