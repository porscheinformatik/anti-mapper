package at.porscheinformatik.antimapper.sample.deletedflag;

public class ChildDTO
{

    private final Integer id;
    private String name;
    private String type;

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

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return String.format("ChildDTO (id=%s, name=%s, type=%s)", id, name, type);
    }

}
