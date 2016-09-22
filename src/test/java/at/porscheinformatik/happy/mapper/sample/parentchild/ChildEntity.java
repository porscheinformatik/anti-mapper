package at.porscheinformatik.happy.mapper.sample.parentchild;

import at.porscheinformatik.happy.mapper.TestUtils;

public class ChildEntity implements Comparable<ChildEntity>
{

    private Integer id;
    private String name;
    private ChildType type;
    private int ordinal;
    private ParentEntity parent;

    public ChildEntity()
    {
        super();
    }

    public ChildEntity(Integer id, String name, ChildType type, int ordinal)
    {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.ordinal = ordinal;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ChildType getType()
    {
        return type;
    }

    public void setType(ChildType type)
    {
        this.type = type;
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(int ordinal)
    {
        this.ordinal = ordinal;
    }

    public ParentEntity getParent()
    {
        return parent;
    }

    public void setParent(ParentEntity parent)
    {
        this.parent = parent;
    }

    @Override
    public int compareTo(ChildEntity o)
    {
        int result = Integer.compare(ordinal, o.ordinal);

        if (result != 0)
        {
            return result;
        }

        return TestUtils.compare(id, o.id);
    }

    @Override
    public String toString()
    {
        return String.format("ChildEntity (id=%s, name=%s, type=%s, ordinal=%s)", id, name, type, ordinal);
    }

}
