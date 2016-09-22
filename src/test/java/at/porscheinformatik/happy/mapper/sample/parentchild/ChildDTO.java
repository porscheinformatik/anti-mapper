package at.porscheinformatik.happy.mapper.sample.parentchild;

public class ChildDTO
{

    private final Integer id;
    private final String name;
    private final ChildType type;

    public ChildDTO(Integer id, String name, ChildType type)
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

    public ChildType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return String.format("ChildDTO (id=%s, name=%s, type=%s)", id, name, type);
    }

}
