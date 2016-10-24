package at.porscheinformatik.antimapper.sample.deletedflag;

public class ChildDTO
{

    private final Integer id;
    private String name;

    public ChildDTO(Integer id, String name)
    {
        super();

        this.id = id;
        this.name = name;
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

    @Override
    public String toString()
    {
        return String.format("ChildDTO (id=%s, name=%s)", id, name);
    }

}
