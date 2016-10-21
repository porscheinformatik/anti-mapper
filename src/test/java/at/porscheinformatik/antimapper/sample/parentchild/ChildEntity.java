package at.porscheinformatik.antimapper.sample.parentchild;

import at.porscheinformatik.antimapper.TestUtils;

public class ChildEntity implements Comparable<ChildEntity>
{

    private Integer id;
    private String key;
    private ChildType type;
    private int ordinal;
    private ParentEntity parent;

    public ChildEntity()
    {
        super();
    }

    public ChildEntity(Integer id, String key, ChildType type, int ordinal)
    {
        super();
        this.id = id;
        this.key = key;
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

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
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
        return String.format("ChildEntity (id=%s, name=%s, type=%s, ordinal=%s)", id, key, type, ordinal);
    }

}
