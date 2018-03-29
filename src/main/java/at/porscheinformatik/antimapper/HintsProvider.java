package at.porscheinformatik.antimapper;

/**
 * The class provides default hints
 *
 * @author HAM
 */
public interface HintsProvider
{

    default Object[] getDefaultHints()
    {
        return null;
    }

}
