package at.porscheinformatik.antimapper;

public class TestUtils
{

    /**
     * Compares the two objects. If one of the objects is null, it will always be greater than the other object. If both
     * objects are null, they are equal.
     *
     * @param <Any> the type of the object
     * @param left the first object
     * @param right the second object
     * @return the result of the compare function
     */
    public static <Any extends Comparable<Any>> int compare(Any left, Any right)
    {
        if (left == null)
        {
            if (right != null)
            {
                return 1;
            }
        }
        else
        {
            if (right != null)
            {
                return left.compareTo(right);
            }

            return -1;
        }

        return 0;
    }

}
