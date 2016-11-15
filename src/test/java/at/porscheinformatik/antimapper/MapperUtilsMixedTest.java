package at.porscheinformatik.antimapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class MapperUtilsMixedTest extends AbstractMapperUtilsTest
{

    @Test
    public void testSingleSame()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "B", "C");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapMixed(sourceList, targetList, MapperUtilsMixedTest::matches, MapperUtilsMixedTest::map,
            MapperUtilsMixedTest::nullFilter, null);

        assertThat(targetList, notNullValue());
        assertThat(targetList.size(), equalTo(3));

        assertAny(targetList, Change.SAME, "A");
        assertAny(targetList, Change.SAME, "B");
        assertAny(targetList, Change.SAME, "C");
    }

    @Test
    public void testSingleSameAndNull()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "!", "C");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapMixed(sourceList, targetList, MapperUtilsMixedTest::matches, MapperUtilsMixedTest::map,
            MapperUtilsMixedTest::nullFilter, null);

        assertThat(targetList, notNullValue());
        assertThat(targetList.size(), equalTo(2));

        assertAny(targetList, Change.SAME, "A");
        assertAny(targetList, Change.SAME, "C");
    }

    @Test
    public void testSingleSameAndKeepNull()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "!", "C");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapMixed(sourceList, targetList, MapperUtilsMixedTest::matches, MapperUtilsMixedTest::map, null,
            null);

        assertThat(targetList, notNullValue());
        assertThat(targetList.size(), equalTo(3));

        assertAny(targetList, Change.SAME, "A");
        assertAny(targetList, Change.SAME, null);
        assertAny(targetList, Change.SAME, "C");
    }

    @Test
    public void testRandom()
    {
        System.out.println("Performing random mixed test ...");

        IntStream.range(0, 65536).parallel().forEach(i -> testSample(i));
    }

    private void testSample(int sample)
    {
        int count = (int) (Math.random() * (2 + sample / 4096));
        List<String> sourceItems = new ArrayList<>();
        List<String> targetItems = new ArrayList<>();

        createRandomItems(sourceItems, targetItems, count);

        // System.out.println("Random mixed test: " + sourceItems + " into " + targetItems);

        Collection<SourceItem> sourceList = createSourceList(sourceItems.toArray(new String[sourceItems.size()]));
        Collection<TargetItem> targetList = createTargetList(targetItems.toArray(new String[targetItems.size()]));

        MapperUtils.mapMixed(sourceList, targetList, MapperUtilsMixedTest::matches, MapperUtilsMixedTest::map,
            MapperUtilsMixedTest::nullFilter, null);

        assertThat(targetList.size(), equalTo(countNotNull(sourceItems)));

        Iterator<String> sourceIterator = sourceItems.iterator();

        while (sourceIterator.hasNext())
        {
            String sourceItem = sourceIterator.next();

            if ("!".equals(sourceItem))
            {
                assertNo(targetList, "!");

                continue;
            }

            assertAny(targetList, null, toKey(sourceItem));
        }
    }

}
