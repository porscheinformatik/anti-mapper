package at.porscheinformatik.antimapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class MapperUtilsOrderedTest extends AbstractMapperUtilsTest
{

    @Test
    public void testSingleSame()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "B", "C");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateSame()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "A", "A", "B", "B", "B", "C", "C", "C");
        Collection<TargetItem> targetList = createTargetList("A", "A", "A", "B", "B", "B", "C", "C", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testSingleAdds()
    {
        Collection<SourceItem> sourceList = createSourceList("1", "A", "2", "B", "3", "C", "4");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.ADDED, "2");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.ADDED, "3");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.ADDED, "4");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateAdds()
    {
        Collection<SourceItem> sourceList =
            createSourceList("1", "1", "A", "A", "2", "2", "B", "B", "3", "3", "C", "C", "4", "4");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.ADDED, "A");
        assertNext(iterator, Change.ADDED, "2");
        assertNext(iterator, Change.ADDED, "2");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.ADDED, "B");
        assertNext(iterator, Change.ADDED, "3");
        assertNext(iterator, Change.ADDED, "3");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.ADDED, "C");
        assertNext(iterator, Change.ADDED, "4");
        assertNext(iterator, Change.ADDED, "4");
        assertNoNext(iterator);
    }

    @Test
    public void testSingleRemoves()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "B", "C");
        Collection<TargetItem> targetList = createTargetList("1", "A", "2", "B", "3", "C", "4");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateRemoves()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "B", "C");
        Collection<TargetItem> targetList =
            createTargetList("1", "1", "A", "A", "2", "2", "B", "B", "3", "3", "C", "C", "4", "4");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testSingleExchanges()
    {
        Collection<SourceItem> sourceList = createSourceList("1", "B", "C2", "D", "3");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C_", "D", "E");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.UPDATED, "C2");
        assertNext(iterator, Change.SAME, "D");
        assertNext(iterator, Change.ADDED, "3");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateExchanges()
    {
        Collection<SourceItem> sourceList = createSourceList("1", "1", "B", "B", "C2", "C2", "D", "D", "3", "3");
        Collection<TargetItem> targetList = createTargetList("A", "A", "B", "B", "C_", "C_", "D", "D", "E", "E");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.UPDATED, "C2");
        assertNext(iterator, Change.UPDATED, "C2");
        assertNext(iterator, Change.SAME, "D");
        assertNext(iterator, Change.SAME, "D");
        assertNext(iterator, Change.ADDED, "3");
        assertNext(iterator, Change.ADDED, "3");
        assertNoNext(iterator);
    }

    @Test
    public void testSingleReverse()
    {
        Collection<SourceItem> sourceList = createSourceList("C", "B", "A");
        Collection<TargetItem> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateReverse()
    {
        Collection<SourceItem> sourceList = createSourceList("C", "C", "B", "B", "A", "A");
        Collection<TargetItem> targetList = createTargetList("A", "A", "B", "B", "C", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNoNext(iterator);
    }

    @Test
    public void testLongestSubList()
    {
        Collection<SourceItem> sourceList =
            createSourceList("@", "1", "2", "3", "A", "B", "C", "B", "A", "X", "Y", "Z", "!");
        Collection<TargetItem> targetList =
            createTargetList("@", "X", "Y", "Z", "A", "B", "C", "B", "A", "1", "2", "3", "!");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "@");
        assertNext(iterator, Change.SAME, "1");
        assertNext(iterator, Change.SAME, "2");
        assertNext(iterator, Change.SAME, "3");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "X");
        assertNext(iterator, Change.SAME, "Y");
        assertNext(iterator, Change.SAME, "Z");
        assertNoNext(iterator);
    }

    @Test
    public void testUpdate()
    {
        Collection<SourceItem> sourceList = createSourceList("A1", "B2", "C3");
        Collection<TargetItem> targetList = createTargetList("A_", "B_", "C_");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.UPDATED, "A1");
        assertNext(iterator, Change.UPDATED, "B2");
        assertNext(iterator, Change.UPDATED, "C3");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife()
    {
        Collection<SourceItem> sourceList = createSourceList("Bs", "Cs", "Es", "As", "Ds", "Fs");
        Collection<TargetItem> targetList = createTargetList("At", "Bt", "Ct", "Dt");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.UPDATED, "Bs");
        assertNext(iterator, Change.UPDATED, "Cs");
        assertNext(iterator, Change.ADDED, "Es");
        assertNext(iterator, Change.UPDATED, "As");
        assertNext(iterator, Change.UPDATED, "Ds");
        assertNext(iterator, Change.ADDED, "Fs");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife2()
    {
        Collection<SourceItem> sourceList = createSourceList("As", "Bs", "Cs", "Ds");
        Collection<TargetItem> targetList = createTargetList("Bt", "Ct", "Et", "At", "Dt", "Ft");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.UPDATED, "As");
        assertNext(iterator, Change.UPDATED, "Bs");
        assertNext(iterator, Change.UPDATED, "Cs");
        assertNext(iterator, Change.UPDATED, "Ds");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife3()
    {
        Collection<SourceItem> sourceList = createSourceList("As", "As");
        Collection<TargetItem> targetList = createTargetList("Ct", "At");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.UPDATED, "As");
        assertNext(iterator, Change.ADDED, "As");
        assertNoNext(iterator);
    }

    @Test
    public void testEmptyTarget()
    {
        Collection<SourceItem> sourceList = createSourceList("As");
        Collection<TargetItem> targetList = createTargetList();

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "As");
        assertNoNext(iterator);
    }

    @Test
    public void testEmptySource()
    {
        Collection<SourceItem> sourceList = createSourceList();
        Collection<TargetItem> targetList = createTargetList("At");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNoNext(iterator);
    }

    @Test
    public void testRealLife4()
    {
        Collection<SourceItem> sourceList = createSourceList("1", "A", "2");
        Collection<TargetItem> targetList = createTargetList("A");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.ADDED, "2");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife5()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "B", "B");
        Collection<TargetItem> targetList = createTargetList("B", "B", "A", "A", "B", "B");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "B");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife6()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "A", "B", "B");
        Collection<TargetItem> targetList = createTargetList("B", "A", "A", "A");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.ADDED, "B");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife7()
    {
        Collection<SourceItem> sourceList = createSourceList("A", "A", "B", "B");
        Collection<TargetItem> targetList = createTargetList("B", "A", "A");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.ADDED, "B");
        assertNoNext(iterator);
    }

    @Test
    public void testRealLife8()
    {
        Collection<SourceItem> sourceList = createSourceList("!", "B", "C");
        Collection<TargetItem> targetList = createTargetList("A");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        Iterator<TargetItem> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "B");
        assertNext(iterator, Change.ADDED, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testRandom()
    {
        System.out.println("Performing random ordered test ...");
        
        IntStream.range(0, 65536).parallel().forEach(i -> testSample(i));
    }

    private void testSample(int sample)
    {
        int count = (int) (Math.random() * (2 + sample / 4096));
        List<String> sourceLines = new ArrayList<>();
        List<String> targetLines = new ArrayList<>();

        createRandomItems(sourceLines, targetLines, count);

        // System.out.println("Random ordered test: " + sourceLines + " into " + targetLines);

        Collection<SourceItem> sourceList = createSourceList(sourceLines.toArray(new String[sourceLines.size()]));
        Collection<TargetItem> targetList = createTargetList(targetLines.toArray(new String[targetLines.size()]));

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsOrderedTest::matches, MapperUtilsOrderedTest::map,
            MapperUtilsOrderedTest::nullFilter, null);

        assertThat(targetList.size(), equalTo(countNotNull(sourceLines)));

        Iterator<String> sourceIterator = sourceLines.iterator();
        Iterator<TargetItem> targetIterator = targetList.iterator();

        while (sourceIterator.hasNext())
        {
            String sourceItem = sourceIterator.next();

            if ("!".equals(sourceItem))
            {
                assertNo(targetList, "!");

                continue;
            }

            assertThat(targetIterator.next().getText(), equalTo(sourceItem));
        }
    }
}
