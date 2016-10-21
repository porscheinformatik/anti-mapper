package at.porscheinformatik.antimapper.sample.parentchild;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

public class ParentChildTest
{

    private final ParentMapper mapper = new ParentMapper(new ChildMapper());

    @Test
    public void test()
    {
        ParentEntity intialEntity = new ParentEntity(1, "Parent", new ChildEntity(1, "A1", ChildType.A, 1),
            new ChildEntity(2, "A3", ChildType.A, 3), new ChildEntity(3, "B2", ChildType.B, 2),
            new ChildEntity(4, "B1", ChildType.B, 1), new ChildEntity(5, "C1", ChildType.C, 1),
            new ChildEntity(6, "A2", ChildType.A, 2));

        System.out.println(intialEntity);

        ParentDTO dto = mapper.transform(intialEntity, ZoneId.systemDefault());

        System.out.println(dto);

        verifyDTO(intialEntity, dto);

        ParentEntity entity = mapper.merge(dto, null, ZoneId.systemDefault());

        System.out.println(entity);

        verifyEntity(intialEntity, entity);
    }

    protected void verifyDTO(ParentEntity intialEntity, ParentDTO dto)
    {
        assertThat(dto, notNullValue());
        assertThat(dto.getId(), equalTo(1));
        assertThat(dto.getName(), equalTo("Parent"));
        assertThat(Date.from(dto.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()),
            equalTo(intialEntity.getTimestamp()));

        assertThat(dto.getChilds(), notNullValue());
        assertThat(dto.getChilds().size(), equalTo(3));

        assertThat(dto.getChilds().get(ChildType.A), notNullValue());
        assertThat(dto.getChilds().get(ChildType.A).size(), equalTo(3));

        assertThat(dto.getChilds().get(ChildType.A).get(0), notNullValue());
        assertThat(dto.getChilds().get(ChildType.A).get(0).getId(), equalTo(1));
        assertThat(dto.getChilds().get(ChildType.A).get(0).getName(), equalTo("A1"));
        assertThat(dto.getChilds().get(ChildType.A).get(0).getType(), equalTo(ChildType.A.name()));

        assertThat(dto.getChilds().get(ChildType.A).get(1), notNullValue());
        assertThat(dto.getChilds().get(ChildType.A).get(1).getId(), equalTo(6));
        assertThat(dto.getChilds().get(ChildType.A).get(1).getName(), equalTo("A2"));
        assertThat(dto.getChilds().get(ChildType.A).get(1).getType(), equalTo(ChildType.A.name()));

        assertThat(dto.getChilds().get(ChildType.A).get(2), notNullValue());
        assertThat(dto.getChilds().get(ChildType.A).get(2).getId(), equalTo(2));
        assertThat(dto.getChilds().get(ChildType.A).get(2).getName(), equalTo("A3"));
        assertThat(dto.getChilds().get(ChildType.A).get(2).getType(), equalTo(ChildType.A.name()));

        assertThat(dto.getChilds().get(ChildType.B), notNullValue());
        assertThat(dto.getChilds().get(ChildType.B).size(), equalTo(2));

        assertThat(dto.getChilds().get(ChildType.B).get(0), notNullValue());
        assertThat(dto.getChilds().get(ChildType.B).get(0).getId(), equalTo(4));
        assertThat(dto.getChilds().get(ChildType.B).get(0).getName(), equalTo("B1"));
        assertThat(dto.getChilds().get(ChildType.B).get(0).getType(), equalTo(ChildType.B.name()));

        assertThat(dto.getChilds().get(ChildType.B).get(1), notNullValue());
        assertThat(dto.getChilds().get(ChildType.B).get(1).getId(), equalTo(3));
        assertThat(dto.getChilds().get(ChildType.B).get(1).getName(), equalTo("B2"));
        assertThat(dto.getChilds().get(ChildType.B).get(1).getType(), equalTo(ChildType.B.name()));

        assertThat(dto.getChilds().get(ChildType.C), notNullValue());
        assertThat(dto.getChilds().get(ChildType.C).size(), equalTo(1));

        assertThat(dto.getChilds().get(ChildType.C).get(0), notNullValue());
        assertThat(dto.getChilds().get(ChildType.C).get(0).getId(), equalTo(5));
        assertThat(dto.getChilds().get(ChildType.C).get(0).getName(), equalTo("C1"));
        assertThat(dto.getChilds().get(ChildType.C).get(0).getType(), equalTo(ChildType.C.name()));
    }

    protected void verifyEntity(ParentEntity intialEntity, ParentEntity entity)
    {
        assertThat(entity, notNullValue());
        assertThat(entity.getId(), equalTo(1));
        assertThat(entity.getKey(), equalTo("Parent"));
        assertThat(entity.getTimestamp(), equalTo(intialEntity.getTimestamp()));

        assertThat(entity.getChilds(), notNullValue());
        assertThat(entity.getChilds().size(), equalTo(6));

        Map<Integer, ChildEntity> childEntities =
            entity.getChilds().stream().collect(Collectors.toMap(child -> child.getId(), Function.identity()));

        assertThat(childEntities.get(1), notNullValue());
        assertThat(childEntities.get(1).getKey(), equalTo("A1"));
        assertThat(childEntities.get(1).getType(), equalTo(ChildType.A));
        assertThat(childEntities.get(1).getOrdinal() < childEntities.get(6).getOrdinal(), equalTo(true));

        assertThat(childEntities.get(2), notNullValue());
        assertThat(childEntities.get(2).getKey(), equalTo("A3"));
        assertThat(childEntities.get(2).getType(), equalTo(ChildType.A));
        assertThat(childEntities.get(2).getOrdinal() > childEntities.get(6).getOrdinal(), equalTo(true));

        assertThat(childEntities.get(3), notNullValue());
        assertThat(childEntities.get(3).getKey(), equalTo("B2"));
        assertThat(childEntities.get(3).getType(), equalTo(ChildType.B));
        assertThat(childEntities.get(3).getOrdinal() > childEntities.get(4).getOrdinal(), equalTo(true));

        assertThat(childEntities.get(4), notNullValue());
        assertThat(childEntities.get(4).getKey(), equalTo("B1"));
        assertThat(childEntities.get(4).getType(), equalTo(ChildType.B));
        assertThat(childEntities.get(4).getOrdinal() < childEntities.get(3).getOrdinal(), equalTo(true));

        assertThat(childEntities.get(5), notNullValue());
        assertThat(childEntities.get(5).getKey(), equalTo("C1"));
        assertThat(childEntities.get(5).getType(), equalTo(ChildType.C));

        assertThat(childEntities.get(6), notNullValue());
        assertThat(childEntities.get(6).getKey(), equalTo("A2"));
        assertThat(childEntities.get(6).getType(), equalTo(ChildType.A));
    }

}
