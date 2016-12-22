package at.porscheinformatik.antimapper.sample.parentchild;

import static at.porscheinformatik.antimapper.TestUtils.*;

import java.util.Date;
import java.util.SortedSet;

public class ParentEntity
{

    private Integer id;
    private String key;
    private Date timestamp;
    private SortedSet<ChildEntity> childs;

    public ParentEntity()
    {
        super();
    }

    public ParentEntity(Integer id)
    {
        super();
        
        this.id = id;
    }

    public ParentEntity(Integer id, String key, ChildEntity... childs)
    {
        super();

        this.id = id;
        this.key = key;

        timestamp = new Date();

        this.childs = toSortedSet(childs);

        for (ChildEntity child : childs)
        {
            child.setParent(this);
        }
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

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public SortedSet<ChildEntity> getChilds()
    {
        return childs;
    }

    public void setChilds(SortedSet<ChildEntity> childs)
    {
        this.childs = childs;
    }

    @Override
    public String toString()
    {
        StringBuilder builder =
            new StringBuilder("ParentEntity (id=").append(id).append(", key=").append(key).append(") {");

        for (ChildEntity child : childs)
        {
            builder.append("\n\t").append(child);
        }

        builder.append("\n}");

        return builder.toString();
    }

}
