package at.porscheinformatik.antimapper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class TestUtils
{

    /**
     * A collator set to primary strength, which means 'a', 'A' and '&auml;' is the same
     */
    public static final Collator DICTIONARY_COLLATOR;

    static
    {
        DICTIONARY_COLLATOR = Collator.getInstance();

        DICTIONARY_COLLATOR.setStrength(Collator.PRIMARY);
        DICTIONARY_COLLATOR.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    }

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
                if (left instanceof String && right instanceof String)
                {
                    return DICTIONARY_COLLATOR.compare(left, right);
                }

                return left.compareTo(right);
            }

            return -1;
        }

        return 0;
    }

    /**
     * Create a list
     *
     * @param <Value> the type of values
     * @param values more of the values
     * @return the list
     */
    @SafeVarargs
    public static <Value> List<Value> toList(Value... values)
    {
        List<Value> list = new ArrayList<>();

        if (values != null && values.length > 0)
        {
            for (Value value : values)
            {
                list.add(value);
            }
        }

        return list;
    }

    /**
     * Create a set
     *
     * @param <Value> the type of values
     * @param values more of the values
     * @return the list
     */
    @SafeVarargs
    public static <Value> Set<Value> toSet(Value... values)
    {
        Set<Value> set = new LinkedHashSet<>();

        if (values != null && values.length > 0)
        {
            for (Value value : values)
            {
                set.add(value);
            }
        }

        return set;
    }

    /**
     * Create a set
     *
     * @param <Value> the type of values
     * @param comparator the comparator
     * @param values more of the values
     * @return the list
     */
    @SafeVarargs
    public static <Value> SortedSet<Value> toTreeSet(Comparator<Value> comparator, Value... values)
    {
        SortedSet<Value> set = new TreeSet<>(comparator);

        if (values != null && values.length > 0)
        {
            for (Value value : values)
            {
                set.add(value);
            }
        }

        return set;
    }

    /**
     * Create a set
     *
     * @param <Value> the type of values
     * @param values more of the values
     * @return the list
     */
    @SafeVarargs
    public static <Value extends Comparable<Value>> SortedSet<Value> toTreeSet(Value... values)
    {
        SortedSet<Value> set = new TreeSet<>();

        if (values != null && values.length > 0)
        {
            for (Value value : values)
            {
                set.add(value);
            }
        }

        return set;
    }

    /**
     * Convert key-value-pairs into a map
     *
     * @param <Key> the key type
     * @param <Value> the value type
     * @param key the first key
     * @param value the first value
     * @param moreKeyValuePairs subsequence key-value-pairs
     * @return a map with the keys
     */
    @SuppressWarnings("unchecked")
    public static <Key, Value> Map<Key, Value> toMap(Key key, Value value, Object... moreKeyValuePairs)
    {
        Map<Key, Value> result = new LinkedHashMap<>();

        result.put(key, value);

        if ((moreKeyValuePairs != null) && (moreKeyValuePairs.length > 0))
        {
            if ((moreKeyValuePairs.length % 2) != 0)
            {
                throw new IllegalArgumentException("Odd number of entries as key-value-pairs");
            }

            for (int i = 0; i < moreKeyValuePairs.length; i += 2)
            {
                result.put((Key) moreKeyValuePairs[i], (Value) moreKeyValuePairs[i + 1]);
            }
        }

        return result;
    }

    /**
     * A Hamcrest matcher for maps
     *
     * @param <Key> the type of the keys
     * @param <Value> the type of the values
     * @param map the map with matchers
     * @return the matcher
     */
    public static <Key, Value> Matcher<Map<Key, Value>> matchesMap(Map<Key, Matcher<?>> map)
    {
        return new BaseMatcher<Map<Key, Value>>()
        {

            @Override
            public boolean matches(Object item)
            {
                if (!(item instanceof Map))
                {
                    return false;
                }

                Map<?, ?> otherMap = (Map<?, ?>) item;

                if (otherMap.size() != map.size())
                {
                    return false;
                }

                Iterator<Entry<Key, Matcher<?>>> iterator = map.entrySet().iterator();

                while (iterator.hasNext())
                {
                    Entry<Key, Matcher<?>> entry = iterator.next();
                    Matcher<?> matcher = entry.getValue();
                    Object value = otherMap.get(entry.getKey());

                    if (!matcher.matches(value))
                    {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(map);
            }

        };
    }

    /**
     * A Hamcrest matcher for lists
     *
     * @param <Value> the type of the values
     * @param list the list with matchers
     * @return the matcher
     */
    public static <Value> Matcher<Collection<Value>> matchesList(List<Matcher<?>> list)
    {
        return new BaseMatcher<Collection<Value>>()
        {

            @Override
            public boolean matches(Object item)
            {
                if (!(item instanceof List))
                {
                    return false;
                }

                Collection<?> otherList = (Collection<?>) item;

                if (otherList.size() != list.size())
                {
                    return false;
                }

                Iterator<Matcher<?>> iterator = list.iterator();
                Iterator<?> otherIterator = otherList.iterator();

                while (iterator.hasNext() && otherIterator.hasNext())
                {
                    Matcher<?> matcher = iterator.next();
                    Object otherValue = otherIterator.next();

                    if (!matcher.matches(otherValue))
                    {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(list);
            }

        };
    }

    /**
     * A Hamcrest matcher for collections
     *
     * @param <Value> the type of the values
     * @param collection the collection with matchers
     * @return the matcher
     */
    public static <Value> Matcher<Collection<Value>> matchesCollection(Collection<Matcher<?>> collection)
    {
        return new BaseMatcher<Collection<Value>>()
        {

            @Override
            public boolean matches(Object item)
            {
                if (!(item instanceof Collection))
                {
                    return false;
                }

                Collection<?> otherCollection = (Collection<?>) item;

                if (otherCollection.size() != collection.size())
                {
                    return false;
                }

                List<?> otherList = new ArrayList<>(otherCollection);

                outer: for (Matcher<?> matcher : collection)
                {
                    Iterator<?> iterator = otherList.iterator();

                    while (iterator.hasNext())
                    {
                        Object otherValue = iterator.next();

                        if (matcher.matches(otherValue))
                        {
                            iterator.remove();
                            continue outer;
                        }
                    }

                    return false;
                }

                if (otherList.size() > 0)
                {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(collection);
            }

        };
    }

    /**
     * A Hamcrest matcher for collections
     *
     * @param <Value> the type of the values
     * @param array the array with matchers
     * @return the matcher
     */
    @SafeVarargs
    public static <Value> Matcher<Collection<Value>> matchesArray(Matcher<?>... array)
    {
        return new BaseMatcher<Collection<Value>>()
        {

            @Override
            public boolean matches(Object item)
            {
                if (item == null)
                {
                    return false;
                }

                if (!item.getClass().isArray())
                {
                    return false;
                }

                Object[] otherArray = (Object[]) item;

                if (otherArray.length != array.length)
                {
                    return false;
                }

                for (int i = 0; i < array.length; i++)
                {
                    Matcher<?> matcher = array[i];
                    Object otherValue = otherArray[i];

                    if (!matcher.matches(otherValue))
                    {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendValue(array);
            }

        };
    }

}
