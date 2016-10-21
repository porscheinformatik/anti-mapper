package at.porscheinformatik.antimapper.sample.deletedflag;

import at.porscheinformatik.antimapper.TestUtils;

public class ChildEntity implements Comparable<ChildEntity>
{

    private Integer id;
    private String name;
    private int ordinal;
    private boolean deleted;
    private ParentEntity parent;

    public ChildEntity()
    {
        super();
    }

    public ChildEntity(Integer id)
    {
        super();

        this.id = id;
    }

    public ChildEntity(Integer id, String name, int ordinal)
    {
        super();

        this.id = id;
        this.name = name;
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

    public int getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(int ordinal)
    {
        this.ordinal = ordinal;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
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
        return String.format("ChildEntity (id=%s, name=%s, ordinal=%s, deleted=%s)", id, name, ordinal, deleted);
    }

}
