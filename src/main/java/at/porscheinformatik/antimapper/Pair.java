package at.porscheinformatik.antimapper;

import java.io.Serializable;

/**
 * A pair of two values
 *
 * @author HAM
 *
 * @param <Left> the left value
 * @param <Right> the right value
 */
public class Pair<Left, Right> implements Serializable
{

    private static final long serialVersionUID = 160174997957476433L;

    public static <Left, Right> Pair<Left, Right> of(Left left, Right right)
    {
        return new Pair<>(left, right);
    }

    public static <Left> Left leftOf(Pair<Left, ?> pair)
    {
        return pair != null ? pair.left : null;
    }

    public static <Right> Right rightOf(Pair<?, Right> pair)
    {
        return pair != null ? pair.right : null;
    }

    private final Left left;
    private final Right right;

    public Pair(Left left, Right right)
    {
        super();
        this.left = left;
        this.right = right;
    }

    public Left getLeft()
    {
        return left;
    }

    public Right getRight()
    {
        return right;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof Pair))
        {
            return false;
        }

        Pair<?, ?> other = (Pair<?, ?>) obj;

        if (left == null)
        {
            if (other.left != null)
            {
                return false;
            }
        }
        else if (!left.equals(other.left))
        {
            return false;
        }

        if (right == null)
        {
            if (other.right != null)
            {
                return false;
            }
        }
        else if (!right.equals(other.right))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", left, right);
    }

}
