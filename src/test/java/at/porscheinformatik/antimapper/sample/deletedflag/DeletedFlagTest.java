package at.porscheinformatik.antimapper.sample.deletedflag;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

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

}
