package at.porscheinformatik.antimapper.sample.deletedflag;

public class ChildDTO
{

    private final Integer id;
    private final String name;

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

    @Override
    public String toString()
    {
        return String.format("ChildDTO (id=%s, name=%s)", id, name);
    }

}
