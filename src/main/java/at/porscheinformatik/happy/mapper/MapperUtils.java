package at.porscheinformatik.happy.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilities for mapping complex objects
 *
 * @author ham
 */
public final class MapperUtils
{

    private MapperUtils()
    {
        super();
    }

    /**
     * Maps the source iterable into the target collection. Ignores the order. Searches for existing objects by using
     * the specified match function, which may only match some important keys (maps the object even if the match
     * function returns true). Maps the source entry to the target entry by using the specified map function. The map
     * function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        return mapMixed(sourceIterable, targetCollection, uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the source iterable into the target collection. Ignores the order. Searches for existing objects by using
     * the specified match function, which may only match some important keys (maps the object even if the match
     * function returns true). Maps the source entry to the target entry by using the specified map function. The map
     * function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Consumer<TargetCollection> afterMapConsumer)
    {
        Objects.requireNonNull(targetCollection);

        if (sourceIterable == null)
        {
            targetCollection.clear();

            return targetCollection;
        }

        Map<TargetValue, Void> mappedTargetValues = new IdentityHashMap<>();

        mapMixedUpdate(mappedTargetValues, sourceIterable, targetCollection, uniqueKeyMatchFunction, mapFunction);
        mapMixedDelete(mappedTargetValues, targetCollection);

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetCollection);
        }

        return targetCollection;
    }

    private static <TargetValue, TargetCollection extends Collection<TargetValue>, SourceValue> void mapMixedUpdate(
        Map<TargetValue, Void> mappedTargetValues, Iterable<? extends SourceValue> sourceIterable,
        TargetCollection targetCollection, MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        StreamSupport.stream(sourceIterable.spliterator(), false).forEach(sourceValue -> {
            TargetValue targetValue = null;
            int index = 0;

            for (TargetValue currentTargetValue : targetCollection)
            {
                if (mappedTargetValues.containsKey(currentTargetValue))
                {
                    continue;
                }

                if (uniqueKeyMatchFunction.matches(sourceValue, currentTargetValue))
                {
                    targetValue = currentTargetValue;
                    break;
                }

                index += 1;
            }

            TargetValue newTargetValue = mapFunction.apply(sourceValue, targetValue);

            if (targetValue == null)
            {
                targetCollection.add(newTargetValue);
            }
            else if (targetValue != newTargetValue)
            {
                if (targetCollection instanceof List<?>)
                {
                    ((List<TargetValue>) targetCollection).set(index, newTargetValue);
                }
                else
                {
                    targetCollection.add(newTargetValue);
                }
            }

            mappedTargetValues.put(newTargetValue, null);
        });
    }

    private static <TargetValue, TargetCollection extends Collection<TargetValue>> void mapMixedDelete(
        Map<TargetValue, Void> mappedTargetValues, TargetCollection targetCollection)
    {
        Iterator<TargetValue> targetIterator = targetCollection.iterator();

        while (targetIterator.hasNext())
        {
            if (!mappedTargetValues.containsKey(targetIterator.next()))
            {
                targetIterator.remove();
            }
        }
    }

    /**
     * Maps the source iterable into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        return mapOrdered(sourceIterable, targetCollection, uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the source iterable into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Consumer<List<TargetValue>> afterMapConsumer)
    {
        Objects.requireNonNull(targetCollection);

        List<? extends SourceValue> sourceList = null;

        if (sourceIterable instanceof List<?>)
        {
            sourceList = (List<? extends SourceValue>) sourceIterable;
        }
        else if (sourceIterable instanceof Collection<?>)
        {
            sourceList = new ArrayList<SourceValue>((Collection<? extends SourceValue>) sourceIterable);
        }
        else
        {
            sourceList = StreamSupport.stream(sourceIterable.spliterator(), false).collect(Collectors.toList());
        }

        List<TargetValue> targetList = null;

        if (targetCollection instanceof List<?>)
        {
            // the target is a list - it can be updated directly
            targetList = (List<TargetValue>) targetCollection;
        }
        else
        {
            // the target is a collection - it has to be rebuilt
            targetList = new ArrayList<TargetValue>(targetCollection);
        }

        mapOrdered(sourceList, targetList, uniqueKeyMatchFunction, mapFunction, afterMapConsumer);

        if (targetCollection != targetList)
        {
            // the target is not a list - rebuild it
            targetCollection.clear();
            targetCollection.addAll(targetList);
        }

        return targetCollection;
    }

    /**
     * Maps the source list into the target list. Keeps the order. Searches for existing objects by using the specified
     * match function, which may only match some important keys (maps the object even if the match function returns
     * true). Maps the source entry to the target entry by using the specified map function. The map function must be
     * able to handle null as target value (create a new instance).
     *
     * @param <SourceEntry> the type of the values in the source list
     * @param <TargetValue> the type of the values in the target list
     * @param sourceList the source list, may be null
     * @param targetList the target list, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target list itself
     */
    public static <TargetValue, SourceEntry> List<TargetValue> mapOrdered(List<? extends SourceEntry> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceEntry, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceEntry, TargetValue, TargetValue> mapFunction)
    {
        return mapOrdered(sourceList, targetList, uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the source list into the target list. Keeps the order. Searches for existing objects by using the specified
     * match function, which may only match some important keys (maps the object even if the match function returns
     * true). Maps the source entry to the target entry by using the specified map function. The map function must be
     * able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source list
     * @param <TargetValue> the type of the values in the target list
     * @param sourceList the source list, may be null
     * @param targetList the target list, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target list itself
     */
    public static <SourceValue, TargetValue> List<TargetValue> mapOrdered(List<? extends SourceValue> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Consumer<List<TargetValue>> afterMapConsumer)
    {
        int[][] table = buildLCSTable(sourceList, targetList, uniqueKeyMatchFunction);

        int sourceIndex = 0;
        int targetIndex = 0;
        int sourceSize = (sourceList != null) ? sourceList.size() : 0;
        int targetSize = targetList.size();
        int writeIndex = 0;
        Collection<TargetValue> removedTargetValues = new ArrayList<>();

        while ((sourceIndex < sourceSize) && (targetIndex < targetSize))
        {
            SourceValue sourceValue = sourceList.get(sourceIndex);
            TargetValue targetValue = targetList.get(writeIndex);

            if (uniqueKeyMatchFunction.matches(sourceValue, targetValue))
            {
                // exists
                targetList.set(writeIndex, mapFunction.apply(sourceValue, targetValue));

                sourceIndex++;
                targetIndex++;
                writeIndex++;
            }
            else if (table[sourceIndex + 1][targetIndex] >= table[sourceIndex][targetIndex + 1])
            {
                // added
                targetValue = removedTargetValues
                    .stream()
                    .filter(removedValue -> uniqueKeyMatchFunction.matches(sourceValue, removedValue))
                    .findFirst()
                    .orElse(null);

                targetList.add(writeIndex, mapFunction.apply(sourceValue, targetValue));

                sourceIndex++;
                writeIndex++;
            }
            else
            {
                // removed
                removedTargetValues.add(targetList.remove(writeIndex));

                targetIndex++;
            }
        }

        // remove remaining
        while (writeIndex < targetList.size())
        {
            removedTargetValues.add(targetList.remove(writeIndex));
        }

        // add remaining
        while (sourceIndex < sourceSize)
        {
            SourceValue sourceValue = sourceList.get(sourceIndex);
            TargetValue targetValue = removedTargetValues
                .stream()
                .filter(removedValue -> uniqueKeyMatchFunction.matches(sourceValue, removedValue))
                .findFirst()
                .orElse(null);

            targetList.add(mapFunction.apply(sourceValue, targetValue));

            sourceIndex++;
        }

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetList);
        }

        return targetList;
    }

    private static <SourceValue, TargetValue> int[][] buildLCSTable(List<? extends SourceValue> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction)
    {
        int sourceSize = (sourceList != null) ? sourceList.size() : 0;
        int targetSize = targetList.size();
        int[][] table = new int[sourceSize + 1][targetSize + 1];

        for (int sourceIndex = sourceSize; sourceIndex >= 0; sourceIndex--)
        {
            for (int targetIndex = targetSize - 1; targetIndex >= 0; targetIndex--)
            {
                if (sourceIndex >= sourceSize)
                {
                    table[sourceIndex][targetIndex] = 0;
                }
                else if (uniqueKeyMatchFunction.matches(sourceList.get(sourceIndex), targetList.get(targetIndex)))
                {
                    table[sourceIndex][targetIndex] = table[sourceIndex + 1][targetIndex + 1] + 1;
                }
                else
                {
                    table[sourceIndex][targetIndex] =
                        Math.max(table[sourceIndex + 1][targetIndex], table[sourceIndex][targetIndex + 1]);
                }
            }
        }

        return table;
    }

    /**
     * Maps the source iterable into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceIterable the source iterable, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        return mapMixedGroups(sourceIterable, targetMap, groupKeyFunction, createTargetCollectionFunction,
            uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the source iterable into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceIterable the source iterable, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction,
        Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        if (sourceIterable == null)
        {
            targetMap.clear();

            return targetMap;
        }

        Map<GroupKey, List<SourceValue>> sourceMap =
            StreamSupport.stream(sourceIterable.spliterator(), false).collect(Collectors.groupingBy(groupKeyFunction));

        sourceMap.entrySet().forEach(sourceEntry -> {
            TargetCollection targetCollection = targetMap.get(sourceEntry.getKey());

            if (targetCollection == null)
            {
                targetCollection = createTargetCollectionFunction.get();

                targetMap.put(sourceEntry.getKey(), targetCollection);
            }

            mapMixed(sourceEntry.getValue(), targetCollection, uniqueKeyMatchFunction, mapFunction);
        });

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetMap);
        }

        return targetMap;
    }

    /**
     * Maps the source iterable into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceIterable the source iterable, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapOrderedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        return mapOrderedGroups(sourceIterable, targetMap, groupKeyFunction, createTargetCollectionFunction,
            uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the source iterable into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceIterable the source iterable, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapOrderedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction,
        Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        if (sourceIterable == null)
        {
            targetMap.clear();

            return targetMap;
        }

        Map<GroupKey, List<SourceValue>> sourceMap =
            StreamSupport.stream(sourceIterable.spliterator(), false).collect(Collectors.groupingBy(groupKeyFunction));

        sourceMap.entrySet().forEach(sourceEntry -> {
            TargetCollection targetCollection = targetMap.get(sourceEntry.getKey());

            if (targetCollection == null)
            {
                targetCollection = createTargetCollectionFunction.get();

                targetMap.put(sourceEntry.getKey(), targetCollection);
            }

            mapMixed(sourceEntry.getValue(), targetCollection, uniqueKeyMatchFunction, mapFunction);
        });

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetMap);
        }

        return targetMap;
    }

    /**
     * Maps the collections of the source map into the target list (performs an un-grouping operation). Keeps the order
     * of the collection. Searches for existing objects by using the specified match function, which may only match some
     * important keys (maps the object even if the match function returns true). Maps the source entry to the target
     * entry by using the specified map function. The map function must be able to handle null as target value (create a
     * new instance).
     *
     * @param <SourceValue> the type of the values in the source collection
     * @param <TargetValue> the type of the values in the target map
     * @param sourceMap the source map, may be null
     * @param targetCollection the target map, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @return the target collection itself
     */
    public static <SourceValue, TargetValue> Collection<TargetValue> mapOrdered(
        Map<?, List<? extends SourceValue>> sourceMap, Collection<TargetValue> targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction)
    {
        return mapOrdered(sourceMap, targetCollection, uniqueKeyMatchFunction, mapFunction, null);
    }

    /**
     * Maps the collections of the source map into the target list (performs an un-grouping operation). Keeps the order
     * of the collection. Searches for existing objects by using the specified match function, which may only match some
     * important keys (maps the object even if the match function returns true). Maps the source entry to the target
     * entry by using the specified map function. The map function must be able to handle null as target value (create a
     * new instance).
     *
     * @param <SourceValue> the type of the values in the source collection
     * @param <TargetValue> the type of the values in the target map
     * @param sourceMap the source map, may be null
     * @param targetCollection the target map, may not be null
     * @param uniqueKeyMatchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the target object may be null)
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetValue> Collection<TargetValue> mapOrdered(
        Map<?, List<? extends SourceValue>> sourceMap, Collection<TargetValue> targetCollection,
        MatchFunction<SourceValue, TargetValue> uniqueKeyMatchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Consumer<List<TargetValue>> afterMapConsumer)
    {
        if ((sourceMap == null) || (sourceMap.isEmpty()))
        {
            targetCollection.clear();

            return targetCollection;
        }

        List<SourceValue> sourceList = new ArrayList<>();

        sourceMap.values().forEach(sourceCollection -> {
            if (sourceCollection != null)
            {
                sourceList.addAll(sourceCollection);
            }
        });

        return mapOrdered(sourceList, targetCollection, uniqueKeyMatchFunction, mapFunction, afterMapConsumer);
    }

    /**
     * Returns the name of the class of the object in a short and readable form.
     *
     * @param obj the object, may be null
     * @return the name
     */
    public static String toClassName(Object obj)
    {
        if (obj == null)
        {
            return toClassName(Void.class);
        }

        return toClassName(obj.getClass());
    }

    /**
     * Returns the name of the class in a short and readable form.
     *
     * @param type the class, may be null
     * @return the name
     */
    public static String toClassName(Class<?> type)
    {
        if (type == null)
        {
            return "?";
        }

        String name = "";

        while (type.isArray())
        {
            name = "[]" + name;
            type = type.getComponentType();
        }

        if (type.isPrimitive())
        {
            name = type.getName() + name;
        }
        else
        {
            name = getShortName(type.getName()) + name;
        }

        return name;
    }

    private static String getShortName(String currentName)
    {
        int beginIndex = currentName.lastIndexOf('.');

        if (beginIndex >= 0)
        {
            currentName = currentName.substring(beginIndex + 1);
        }
        return currentName;
    }

    public static String abbreviate(String s, int length)
    {
        if (s == null)
        {
            return null;
        }

        if (s.length() < length)
        {
            return s;
        }

        if (length < 3)
        {
            throw new IllegalArgumentException("Length must be >= 3");
        }

        return s.substring(0, s.length() - 3) + "...";
    }

}
