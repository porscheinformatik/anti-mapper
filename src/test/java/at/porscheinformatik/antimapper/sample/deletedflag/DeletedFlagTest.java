package at.porscheinformatik.antimapper.sample.deletedflag;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DeletedFlagTest
{

    private final ChildMapper childMapper = new ChildMapper();
    private final ParentMapper parentMapper = new ParentMapper(childMapper);

    @Test
    public void testTransformParent()
    {
        ParentEntity entity = new ParentEntity(1, "Parent#1");

        ParentDTO dto = parentMapper.transform(entity);

        assertThat(dto, notNullValue());
        assertThat(dto.getId(), equalTo(1));
        assertThat(dto.getName(), equalTo("Parent#1"));
    }

    @Test
    public void testMergeParent()
    {
        ParentDTO dto = new ParentDTO(1, "Parent#1");
        ParentEntity entity = parentMapper.merge(dto, null);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1));
        assertThat(entity.getName(), equalTo("Parent#1"));
        assertThat(entity.isDeleted(), equalTo(false));
    }

    @Test
    public void testMergeNonNullParent()
    {
        ParentDTO dto = new ParentDTO(1, "Parent#1");
        ParentEntity entity = new ParentEntity(1, "Parent#?");

        entity.setDeleted(true);

        entity = parentMapper.merge(dto, null);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1));
        assertThat(entity.getName(), equalTo("Parent#1"));
        assertThat(entity.isDeleted(), equalTo(false));
    }

    @Test
    public void testTransformDeletedParent()
    {
        ParentEntity entity = new ParentEntity(1, "Parent#1");

        entity.setDeleted(true);

        ParentDTO dto = parentMapper.transform(entity);

        assertThat(dto, nullValue());
    }

    @Test
    public void testMergeDeletedParent()
    {
        ParentEntity entity = new ParentEntity(1, "Parent#1");

        assertThat(entity.isDeleted(), equalTo(false));

        entity = parentMapper.merge(null, entity);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1));
        assertThat(entity.getName(), equalTo("Parent#1"));
        assertThat(entity.isDeleted(), equalTo(true));
    }

    @Test
    public void testDelete()
    {
        ChildEntity child1 = new ChildEntity(1, "Child#1", ChildType.A, 0);
        ChildEntity child2 = new ChildEntity(2, "Child#2", ChildType.B, 1);
        ChildEntity child3 = new ChildEntity(3, "Child#3", ChildType.C, 2);

        child2.setDeleted(true);

        ParentEntity entity = new ParentEntity(1, "Parent#1", child1, child2, child3);

        ParentDTO dto = parentMapper.transform(entity);

        assertThat(dto, notNullValue());
        assertThat(dto.getId(), equalTo(1));
        assertThat(dto.getName(), equalTo("Parent#1"));

        List<ChildDTO> childs = dto.getChilds();

        assertThat(childs, notNullValue());
        assertThat(childs.size(), equalTo(2));

        assertThat(childs.get(0), notNullValue());
        assertThat(childs.get(0).getId(), equalTo(1));
        assertThat(childs.get(0).getName(), equalTo("Child#1"));

        childs.get(0).setName("Updated Child#1");

        assertThat(childs.get(1), notNullValue());
        assertThat(childs.get(1).getId(), equalTo(3));
        assertThat(childs.get(1).getName(), equalTo("Child#3"));

        childs.get(1).setName("Updated Child#3");

        entity = parentMapper.merge(dto, entity);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1));
        assertThat(entity.getName(), equalTo("Parent#1"));

        List<ChildEntity> childEntities = new ArrayList<>(entity.getChilds());

        assertThat(childEntities.size(), equalTo(3));

        assertThat(childEntities.get(0), notNullValue());
        assertThat(childEntities.get(0).getId(), equalTo(1));
        assertThat(childEntities.get(0).getName(), equalTo("Updated Child#1"));

        assertThat(childEntities.get(1), notNullValue());
        assertThat(childEntities.get(1).getId(), equalTo(2));
        assertThat(childEntities.get(1).getName(), equalTo("Child#2"));
        assertThat(childEntities.get(1).isDeleted(), equalTo(true));

        assertThat(childEntities.get(2), notNullValue());
        assertThat(childEntities.get(2).getId(), equalTo(3));
        assertThat(childEntities.get(2).getName(), equalTo("Updated Child#3"));

    }

}
