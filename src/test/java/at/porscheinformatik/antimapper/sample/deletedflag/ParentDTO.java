package at.porscheinformatik.antimapper.sample.deletedflag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParentDTO
{

    private final Integer id;

    private String name;
    private List<ChildDTO> childs;

    public ParentDTO(Integer id)
    {
        super();

        this.id = id;
    }

    public ParentDTO(Integer id, String name, ChildDTO... childs)
    {
        super();
        this.id = id;
        this.name = name;
        this.childs = childs != null ? new ArrayList<>(Arrays.asList(childs)) : null;
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

    public List<ChildDTO> getChilds()
    {
        return childs;
    }

    public void setChilds(List<ChildDTO> childs)
    {
        this.childs = childs;
    }

    @Override
    public String toString()
    {
        StringBuilder builder =
            new StringBuilder("ParentDTO (id=").append(id).append(", name=").append(name).append(") ").append(childs);

        return builder.toString();
    }

}
