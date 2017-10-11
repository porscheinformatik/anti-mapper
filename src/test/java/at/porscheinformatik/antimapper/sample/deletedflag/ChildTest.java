package at.porscheinformatik.antimapper.sample.deletedflag;

import org.junit.Test;

public class ChildTest
{

    private final ChildMapper childMapper = new ChildMapper();
    private final ParentMapper parentMapper = new ParentMapper(childMapper);

    @Test
    public void test()
    {
        ParentEntity entity = new ParentEntity(1, "Parent", //
            new ChildEntity(1, "Team", ChildType.A, 0), new ChildEntity(2, "Zwei", ChildType.A, 1),
            new ChildEntity(3, "Team", ChildType.B, 0), new ChildEntity(4, "Vier", ChildType.B, 1));

        ParentDTO dto = new ParentDTO(1, "Parent", //
            new ChildDTO(3, "Team", ChildType.B.name()), new ChildDTO(4, "Vier", ChildType.B.name()),
            new ChildDTO(2, "Zwei", ChildType.A.name()));

        entity = parentMapper.merge(dto, entity);

        System.err.println(entity);
    }
}
