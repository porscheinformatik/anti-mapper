package at.porscheinformatik.happy.mapper.sample.parentchild;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ParentDTO
{

    private final Integer id;

    private String name;
    private LocalDateTime timestamp;
    private Map<ChildType, List<ChildDTO>> childs;

    public ParentDTO(Integer id)
    {
        super();

        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public Map<ChildType, List<ChildDTO>> getChilds()
    {
        return childs;
    }

    public void setChilds(Map<ChildType, List<ChildDTO>> childs)
    {
        this.childs = childs;
    }

    @Override
    public String toString()
    {
        StringBuilder builder =
            new StringBuilder("ParentDTO (id=").append(id).append(", name=").append(name).append(") {");

        for (Entry<ChildType, List<ChildDTO>> entry : childs.entrySet())
        {
            builder.append("\n\t").append(entry.getKey()).append(": [");

            Iterator<ChildDTO> iterator = entry.getValue().iterator();

            while (iterator.hasNext())
            {
                builder.append(iterator.next());

                if (iterator.hasNext())
                {
                    builder.append(", ");
                }
            }

            builder.append("]");
        }

        builder.append("\n}");

        return builder.toString();
    }

}
