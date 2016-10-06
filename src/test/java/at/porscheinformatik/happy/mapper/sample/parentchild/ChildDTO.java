package at.porscheinformatik.happy.mapper.sample.parentchild;

public class ChildDTO
{

    private final Integer id;
    private final String name;
    private final String type;

    public ChildDTO(Integer id, String name, String type)
    {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return String.format("ChildDTO (id=%s, name=%s, type=%s)", id, name, type);
    }

}
