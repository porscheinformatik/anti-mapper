package at.porscheinformatik.antimapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
     * Transforms the collection to an unmodifiable one, if there is any implementation in the Java {@link Collections}
     * utilities.
     *
     * @param <AnyCollection> the type of collection
     * @param collection the collection
     * @return the collection
     */
    @SuppressWarnings("unchecked")
    public static <AnyCollection extends Collection<?>> AnyCollection toUnmodifiableCollection(AnyCollection collection)
    {
        if (collection == null)
        {
            return null;
        }

        if (collection instanceof List<?>)
        {
            return (AnyCollection) Collections.unmodifiableList((List<?>) collection);
        }

        if (collection instanceof NavigableSet<?>)
        {
            return (AnyCollection) Collections.unmodifiableNavigableSet((NavigableSet<?>) collection);
        }

        if (collection instanceof SortedSet<?>)
        {
            return (AnyCollection) Collections.unmodifiableSortedSet((SortedSet<?>) collection);
        }

        if (collection instanceof Set<?>)
        {
            return (AnyCollection) Collections.unmodifiableSet((Set<?>) collection);
        }

        return (AnyCollection) Collections.unmodifiableCollection(collection);
    }

    /**
     * Transforms the map into an unmodifiable one, if there is any implemenation in the Java {@link Collections}
     * utilities.
     *
     * @param <AnyMap> the type of map
     * @param map the map
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public static <AnyMap extends Map<?, ?>> AnyMap toUnmodifiableMap(AnyMap map)
    {
        if (map == null)
        {
            return null;
        }

        if (map instanceof NavigableMap<?, ?>)
        {
            return (AnyMap) Collections.unmodifiableNavigableMap((NavigableMap<?, ?>) map);
        }

        if (map instanceof SortedMap<?, ?>)
        {
            return (AnyMap) Collections.unmodifiableSortedMap((SortedMap<?, ?>) map);
        }

        return (AnyMap) Collections.unmodifiableMap(map);
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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<TargetCollection> afterMapConsumer)
    {
        return mapMixed(streamOrNull(sourceIterable), targetCollection, matchFunction, mapFunction, false, filter,
            afterMapConsumer);
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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<TargetCollection> afterMapConsumer)
    {
        return mapMixed(streamOrNull(sourceIterable), targetCollection, matchFunction, mapFunction, keepMissing, filter,
            afterMapConsumer);
    }

    /**
     * Maps the source stream into the target collection. Ignores the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceStream the source stream, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Stream<? extends SourceValue> sourceStream, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<TargetCollection> afterMapConsumer)
    {
        return mapMixed(sourceStream, targetCollection, matchFunction, mapFunction, false, filter, afterMapConsumer);
    }

    /**
     * Maps the source stream into the target collection. Ignores the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceStream the source stream, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapMixed(
        Stream<? extends SourceValue> sourceStream, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<TargetCollection> afterMapConsumer)
    {
        Objects.requireNonNull(targetCollection);

        if (sourceStream == null && !keepMissing)
        {
            targetCollection.clear();

            return targetCollection;
        }

        Map<TargetValue, Void> mappedTargetValues = new IdentityHashMap<>();

        mapMixedUpdate(mappedTargetValues, sourceStream, targetCollection, matchFunction, mapFunction, filter);
        mapMixedDelete(mappedTargetValues, targetCollection, mapFunction, keepMissing, filter);

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetCollection);
        }

        return targetCollection;
    }

    @SuppressWarnings("unchecked")
    private static <TargetValue, TargetCollection extends Collection<TargetValue>, SourceValue> void mapMixedUpdate(
        Map<TargetValue, Void> mappedTargetValues, Stream<? extends SourceValue> sourceStream,
        TargetCollection targetCollection, MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter)
    {
        sourceStream.forEach(sourceValue -> {
            TargetValue targetValue = null;
            int index = 0;

            for (TargetValue currentTargetValue : targetCollection)
            {
                if (mappedTargetValues.containsKey(currentTargetValue))
                {
                    continue;
                }

                if (matchFunction.matches(sourceValue, currentTargetValue))
                {
                    targetValue = currentTargetValue;
                    break;
                }

                index += 1;
            }

            TargetValue newTargetValue = mapFunction.apply(sourceValue, targetValue);

            if (filter != null && !filter.test(newTargetValue))
            {
                return;
            }

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
        Map<TargetValue, Void> mappedTargetValues, TargetCollection targetCollection,
        BiFunction<?, TargetValue, TargetValue> mapFunction, boolean keepMissing, Predicate<TargetValue> filter)
    {
        List<TargetValue> newTargetValues = new ArrayList<>();
        Iterator<TargetValue> targetIterator = targetCollection.iterator();

        while (targetIterator.hasNext())
        {
            TargetValue targetValue = targetIterator.next();

            if (mappedTargetValues.containsKey(targetValue))
            {
                continue;
            }

            TargetValue newTargetValue = keepMissing ? targetValue : mapFunction.apply(null, targetValue);

            if (newTargetValue == null || (filter != null && !filter.test(newTargetValue)))
            {
                targetIterator.remove();

                continue;
            }

            if (targetValue == newTargetValue)
            {
                continue;
            }

            targetIterator.remove();
            newTargetValues.add(newTargetValue);
        }

        if (newTargetValues.size() > 0)
        {
            targetCollection.addAll(newTargetValues);
        }
    }

    /**
     * Maps the source stream into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance). Tries to rescue removed target values by
     * reusing them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceStream the source stream, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Stream<? extends SourceValue> sourceStream, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<List<TargetValue>> afterMapConsumer)
    {
        return mapOrdered(sourceStream != null ? sourceStream.collect(Collectors.toList()) : null, targetCollection,
            matchFunction, mapFunction, false, filter, afterMapConsumer);
    }

    /**
     * Maps the source stream into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance). Tries to rescue removed target values by
     * reusing them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceStream the source stream, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Stream<? extends SourceValue> sourceStream, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<List<TargetValue>> afterMapConsumer)
    {
        return mapOrdered(sourceStream != null ? sourceStream.collect(Collectors.toList()) : null, targetCollection,
            matchFunction, mapFunction, keepMissing, filter, afterMapConsumer);
    }

    /**
     * Maps the source iterable into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance). Tries to rescue removed target values by
     * reusing them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<List<TargetValue>> afterMapConsumer)
    {
        return mapOrdered(sourceIterable, targetCollection, matchFunction, mapFunction, false, filter,
            afterMapConsumer);
    }

    /**
     * Maps the source iterable into the target collection. Keeps the order. Searches for existing objects by using the
     * specified match function, which may only match some important keys (maps the object even if the match function
     * returns true). Maps the source entry to the target entry by using the specified map function. The map function
     * must be able to handle null as target value (create a new instance). Tries to rescue removed target values by
     * reusing them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source iterable
     * @param <TargetCollection> the type of the target collection
     * @param <TargetValue> the type of the values in the target collection
     * @param sourceIterable the source iterable, may be null
     * @param targetCollection the target collection, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    @SuppressWarnings("unchecked")
    public static <SourceValue, TargetCollection extends Collection<TargetValue>, TargetValue> TargetCollection mapOrdered(
        Iterable<? extends SourceValue> sourceIterable, TargetCollection targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<List<TargetValue>> afterMapConsumer)
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
            sourceList = MapperUtils.streamOrEmpty(sourceIterable).collect(Collectors.toList());
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
            targetList = new ArrayList<>(targetCollection);
        }

        mapOrdered(sourceList, targetList, matchFunction, mapFunction, keepMissing, filter, afterMapConsumer);

        if (targetCollection != targetList)
        {
            // the target is not a list - rebuild it
            if (targetCollection.size() > 0)
            {
                targetCollection.clear();
            }

            if (targetList.size() > 0)
            {
                targetCollection.addAll(targetList);
            }
        }

        return targetCollection;
    }

    /**
     * Maps the source list into the target list. Keeps the order. Searches for existing objects by using the specified
     * match function, which may only match some important keys (maps the object even if the match function returns
     * true). Maps the source entry to the target entry by using the specified map function. The map function must be
     * able to handle null as target value (create a new instance). Tries to rescue removed target values by reusing
     * them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source list
     * @param <TargetValue> the type of the values in the target list
     * @param sourceList the source list, may be null
     * @param targetList the target list, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target list itself
     */
    public static <SourceValue, TargetValue> List<TargetValue> mapOrdered(List<? extends SourceValue> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<List<TargetValue>> afterMapConsumer)
    {
        return mapOrdered(sourceList, targetList, matchFunction, mapFunction, false, filter, afterMapConsumer);
    }

    /**
     * Maps the source list into the target list. Keeps the order. Searches for existing objects by using the specified
     * match function, which may only match some important keys (maps the object even if the match function returns
     * true). Maps the source entry to the target entry by using the specified map function. The map function must be
     * able to handle null as target value (create a new instance). Tries to rescue removed target values by reusing
     * them (if the unique key matches).
     *
     * @param <SourceValue> the type of the values in the source list
     * @param <TargetValue> the type of the values in the target list
     * @param sourceList the source list, may be null
     * @param targetList the target list, may not be null
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target list itself
     */
    public static <SourceValue, TargetValue> List<TargetValue> mapOrdered(List<? extends SourceValue> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<List<TargetValue>> afterMapConsumer)
    {
        int[][] table = buildLCSTable(sourceList, targetList, matchFunction);

        int sourceIndex = 0;
        int targetIndex = 0;
        int sourceSize = (sourceList != null) ? sourceList.size() : 0;
        int writeIndex = 0;
        Collection<TargetValue> removedTargetValues = new ArrayList<>();

        while (sourceIndex < sourceSize && writeIndex < targetList.size())
        {
            @SuppressWarnings("null")
            SourceValue sourceValue = sourceList.get(sourceIndex);
            TargetValue targetValue = targetList.get(writeIndex);

            if (matchFunction.matches(sourceValue, targetValue))
            {
                // exists
                TargetValue mappedTargetValue = mapFunction.apply(sourceValue, targetValue);

                if (filter != null && !filter.test(mappedTargetValue))
                {
                    removedTargetValues.add(mappedTargetValue);

                    targetList.remove(writeIndex);
                    sourceIndex++;
                    targetIndex++;

                    continue;
                }

                targetList.set(writeIndex, mappedTargetValue);

                sourceIndex++;
                targetIndex++;
                writeIndex++;

                continue;
            }

            if (table[sourceIndex + 1][targetIndex] >= table[sourceIndex][targetIndex + 1])
            {
                // added
                TargetValue rescuedTargetValue = rescueTargetValue(sourceValue, removedTargetValues, matchFunction);

                if (rescuedTargetValue == null)
                {
                    // rescue a value that will be removed
                    for (int i = writeIndex + 1; i < targetList.size(); i++)
                    {
                        if (matchFunction.matches(sourceValue, targetList.get(i)))
                        {
                            rescuedTargetValue = targetList.remove(i);
                            break;
                        }
                    }
                }

                TargetValue mappedTargetValue = mapFunction.apply(sourceValue, rescuedTargetValue);

                if (filter != null && !filter.test(mappedTargetValue))
                {
                    sourceIndex++;

                    continue;
                }

                targetList.add(writeIndex, mappedTargetValue);

                sourceIndex++;
                writeIndex++;

                continue;
            }

            // removed
            TargetValue mappedTargetValue = keepMissing ? targetValue : mapFunction.apply(null, targetValue);

            removedTargetValues.add(mappedTargetValue);
            targetList.remove(writeIndex);
            targetIndex++;
        }

        // remove remaining
        while (writeIndex < targetList.size())
        {
            TargetValue targetValue = targetList.remove(writeIndex);
            TargetValue mappedTargetValue = keepMissing ? targetValue : mapFunction.apply(null, targetValue);

            removedTargetValues.add(mappedTargetValue);
        }

        // add remaining
        while (sourceIndex < sourceSize)
        {
            @SuppressWarnings("null")
            SourceValue sourceValue = sourceList.get(sourceIndex);
            TargetValue rescuedTargetValue = rescueTargetValue(sourceValue, removedTargetValues, matchFunction);
            TargetValue mappedTargetValue = mapFunction.apply(sourceValue, rescuedTargetValue);

            if (filter != null && !filter.test(mappedTargetValue))
            {
                sourceIndex++;

                continue;
            }

            targetList.add(mappedTargetValue);

            sourceIndex++;
        }

        if (filter != null)
        {
            Iterator<TargetValue> iterator = targetList.iterator();

            while (iterator.hasNext())
            {
                TargetValue next = iterator.next();

                if (!filter.test(next))
                {
                    iterator.remove();
                }
            }
        }

        for (TargetValue targetValue : removedTargetValues)
        {
            if (filter == null || filter.test(targetValue))
            {
                targetList.add(targetValue);
            }
        }

        if (afterMapConsumer != null)
        {
            afterMapConsumer.accept(targetList);
        }

        return targetList;
    }

    private static <TargetValue, SourceValue> TargetValue rescueTargetValue(SourceValue sourceValue,
        Collection<TargetValue> removedTargetValues, MatchFunction<SourceValue, TargetValue> matchFunction)
    {
        TargetValue rescuedTargetValue = null;
        Iterator<TargetValue> iterator = removedTargetValues.iterator();

        while (iterator.hasNext())
        {
            TargetValue currentTargetValue = iterator.next();

            if (sourceValue == currentTargetValue
                || (sourceValue != null
                    && currentTargetValue != null
                    && matchFunction.matches(sourceValue, currentTargetValue)))
            {
                rescuedTargetValue = currentTargetValue;
                iterator.remove();
                break;
            }
        }

        return rescuedTargetValue;
    }

    @SuppressWarnings("null")
    private static <SourceValue, TargetValue> int[][] buildLCSTable(List<? extends SourceValue> sourceList,
        List<TargetValue> targetList, MatchFunction<SourceValue, TargetValue> matchFunction)
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
                else if (matchFunction.matches(sourceList.get(sourceIndex), targetList.get(targetIndex)))
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
     * @param sourceIterable the source stream, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        return mapMixedGroups(streamOrNull(sourceIterable), targetMap, groupKeyFunction, createTargetCollectionFunction,
            matchFunction, mapFunction, false, filter, afterMapConsumer);
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
     * @param sourceIterable the source stream, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        return mapMixedGroups(streamOrNull(sourceIterable), targetMap, groupKeyFunction, createTargetCollectionFunction,
            matchFunction, mapFunction, keepMissing, filter, afterMapConsumer);
    }

    /**
     * Maps the source stream into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source stream
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceStream the source stream, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Stream<? extends SourceValue> sourceStream, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        return mapMixedGroups(sourceStream, targetMap, groupKeyFunction, createTargetCollectionFunction, matchFunction,
            mapFunction, false, filter, afterMapConsumer);
    }

    /**
     * Maps the source stream into the target map. Performs a grouping operation. Keeps the order of the collections.
     * Searches for existing objects by using the specified match function, which may only match some important keys
     * (maps the object even if the match function returns true). Maps the source entry to the target entry by using the
     * specified map function. The map function must be able to handle null as target value (create a new instance).
     *
     * @param <SourceValue> the type of the values in the source stream
     * @param <GroupKey> the type of the key in the target map
     * @param <TargetCollection> the type of the collection in the target map
     * @param <TargetValue> the type of the values in the target map
     * @param sourceStream the source stream, may be null
     * @param targetMap the target map, may not be null
     * @param groupKeyFunction the function extracting the key from a source value
     * @param createTargetCollectionFunction create a new collection entry for the target map
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapMixedGroups(
        Stream<? extends SourceValue> sourceStream, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        if (sourceStream == null && !keepMissing)
        {
            targetMap.clear();

            return targetMap;
        }

        Map<GroupKey, List<SourceValue>> sourceMap = new HashMap<>();

        // this implementation is null-able, in contrast to the groupingBy collector
        if (sourceStream != null)
        {
            sourceStream.forEach(sourceItem -> {
                GroupKey key = groupKeyFunction.apply(sourceItem);
                List<SourceValue> list = sourceMap.get(key);

                if (list == null)
                {
                    list = new ArrayList<>();

                    sourceMap.put(key, list);
                }

                list.add(sourceItem);
            });
        }

        Iterator<Entry<GroupKey, List<SourceValue>>> sourceIterator = sourceMap.entrySet().iterator();

        while (sourceIterator.hasNext())
        {
            Entry<GroupKey, List<SourceValue>> sourceEntry = sourceIterator.next();
            TargetCollection targetCollection = targetMap.get(sourceEntry.getKey());

            if (targetCollection == null)
            {
                targetCollection = createTargetCollectionFunction.get();
            }

            mapMixed(sourceEntry.getValue() != null ? sourceEntry.getValue().stream() : null, targetCollection,
                matchFunction, mapFunction, keepMissing, filter, null);

            if (!targetCollection.isEmpty())
            {
                targetMap.put(sourceEntry.getKey(), targetCollection);
            }
        }

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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapOrderedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        return mapOrderedGroups(sourceIterable, targetMap, groupKeyFunction, createTargetCollectionFunction,
            matchFunction, mapFunction, false, filter, afterMapConsumer);
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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, GroupKey, TargetCollection extends Collection<TargetValue>, TargetValue> Map<GroupKey, TargetCollection> mapOrderedGroups(
        Iterable<? extends SourceValue> sourceIterable, Map<GroupKey, TargetCollection> targetMap,
        Function<SourceValue, GroupKey> groupKeyFunction, Supplier<TargetCollection> createTargetCollectionFunction,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<Map<GroupKey, TargetCollection>> afterMapConsumer)
    {
        if (sourceIterable == null && !keepMissing)
        {
            targetMap.clear();

            return targetMap;
        }

        Map<GroupKey, List<SourceValue>> sourceMap =
            streamOrEmpty(sourceIterable).collect(Collectors.groupingBy(groupKeyFunction));

        sourceMap.entrySet().forEach(sourceEntry -> {
            TargetCollection targetCollection = targetMap.get(sourceEntry.getKey());

            if (targetCollection == null)
            {
                targetCollection = createTargetCollectionFunction.get();

                targetMap.put(sourceEntry.getKey(), targetCollection);
            }

            mapMixed(sourceEntry.getValue() != null ? sourceEntry.getValue().stream() : null, targetCollection,
                matchFunction, mapFunction, keepMissing, filter, null);
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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetValue> Collection<TargetValue> mapOrdered(
        Map<?, List<? extends SourceValue>> sourceMap, Collection<TargetValue> targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, Predicate<TargetValue> filter,
        Consumer<List<TargetValue>> afterMapConsumer)
    {
        return mapOrdered(sourceMap, targetCollection, matchFunction, mapFunction, false, filter, afterMapConsumer);
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
     * @param matchFunction the function to compare the source and the target object
     * @param mapFunction the function to map the source to a target object (the source and the target object may be
     *            null)
     * @param keepMissing true to keep missing items
     * @param filter optional filter for excluding results
     * @param afterMapConsumer optional consumer, executed after a successful mapping
     * @return the target collection itself
     */
    public static <SourceValue, TargetValue> Collection<TargetValue> mapOrdered(
        Map<?, List<? extends SourceValue>> sourceMap, Collection<TargetValue> targetCollection,
        MatchFunction<SourceValue, TargetValue> matchFunction,
        BiFunction<SourceValue, TargetValue, TargetValue> mapFunction, boolean keepMissing,
        Predicate<TargetValue> filter, Consumer<List<TargetValue>> afterMapConsumer)
    {
        if ((sourceMap == null || sourceMap.isEmpty()) && !keepMissing)
        {
            targetCollection.clear();

            return targetCollection;
        }

        List<SourceValue> sourceList = new ArrayList<>();

        if (sourceMap != null)
        {
            sourceMap.values().forEach(sourceCollection -> {
                if (sourceCollection != null)
                {
                    sourceList.addAll(sourceCollection);
                }
            });
        }

        return mapOrdered(sourceList, targetCollection, matchFunction, mapFunction, keepMissing, filter,
            afterMapConsumer);
    }

    /**
     * Cleanups the collection by removing all null entries
     *
     * @param <AnyCollection> the type of collection
     * @param collection the collection
     * @return the same collection for chaining calls
     */
    public static <AnyCollection extends Collection<?>> AnyCollection cleanup(AnyCollection collection)
    {
        Iterator<?> iterator = collection.iterator();

        while (iterator.hasNext())
        {
            if (iterator.next() == null)
            {
                iterator.remove();
            }
        }

        return collection;
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

    public static <Any> Stream<Any> streamOrNull(Iterable<Any> iterable)
    {
        return iterable != null ? StreamSupport.stream(iterable.spliterator(), false) : null;
    }

    public static <Any> Stream<Any> streamOrEmpty(Iterable<Any> iterable)
    {
        return iterable != null ? StreamSupport.stream(iterable.spliterator(), false) : Stream.empty();
    }

    public static <Any> Stream<Any> streamOrEmpty(Stream<Any> stream)
    {
        return stream != null ? stream : Stream.empty();
    }

}
