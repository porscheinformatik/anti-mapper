package at.porscheinformatik.happy.mapper;

/**
 * A exception for mapping errors
 *
 * @author HAM
 */
public class MapperException extends RuntimeException
{

    private static final long serialVersionUID = 3231457877062797559L;

    public MapperException(String message, Throwable cause, Object... args)
    {
        super(String.format(message, args), cause);
    }

    public MapperException(String message, Object... args)
    {
        super(String.format(message, args));
    }

}
