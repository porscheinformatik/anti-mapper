package at.porscheinformatik.antimapper.sample.deletedflag;

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
        // no DTO gets generated from a deleted entity
        if (entity.isDeleted())
        {
            return null;
        }

        ParentDTO dto = new ParentDTO(entity.getId());

        // add the entity and the DTO to the hints, just in case the childMapper needs it
        hints = Hints.join(hints, entity, dto);

        dto.setName(entity.getName());
        dto.setChilds(childMapper.transformToArrayList(entity.getChilds(), hints));
        
        return dto;
    }

    @Override
    protected ParentEntity mergeNull(ParentEntity entity, Object[] hints)
    {
        entity.setDeleted(true);

        return entity;
    }

    @Override
    protected ParentEntity mergeNonNull(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        // add the entity and the DTO to the hints, just in case the childMapper needs it
        hints = Hints.join(hints, entity, dto);

        entity.setName(dto.getName());
        entity.setChilds(childMapper.mergeIntoTreeSet(dto.getChilds(), entity.getChilds(), hints));

        // ensure, that the entity is not deleted, because there was a DTO
        entity.setDeleted(false);
        
        return entity;
    }

    @Override
    protected ParentEntity create(ParentDTO dto, Object... hints)
    {
        return new ParentEntity(dto.getId());
    }

    @Override
    public boolean isUniqueKeyMatching(ParentDTO dto, ParentEntity entity, Object... hints)
    {
        return Objects.equals(dto.getId(), entity.getId());
    }

}
