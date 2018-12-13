package at.porscheinformatik.antimapper.sample.deletedflag;

import java.util.Objects;

import at.porscheinformatik.antimapper.AbstractMapper;

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

        dto.setName(entity.getName());
        dto.setChilds(childMapper.transformAll(entity.getChilds(), hints).toArrayList());

        return dto;
    }

    @Override
    protected ParentEntity mergeNull(ParentEntity entity, Object[] hints)
    {
        entity.setDeleted(true);

        return entity;
    }

    @Override
    protected ParentEntity mergeNonNull(ParentDTO dto, ParentEntity entity, Object[] hints)
    {
        entity.setName(dto.getName());
        entity.setChilds(childMapper.mergeAll(dto.getChilds(), hints).intoTreeSet(entity.getChilds()));

        // ensure, that the entity is not deleted, because there was a DTO
        entity.setDeleted(false);

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
