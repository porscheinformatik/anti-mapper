package at.porscheinformatik.happy.mapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Test;

public class MapperUtilsTest
{

    private enum Change
    {
        SAME,
        ADDED,
        UPDATED
    };

    private abstract static class Line
    {
        private Change change;
        private String text;

        public Line(String text)
        {
            this(Change.SAME, text);
        }

        public Line(Change change, String text)
        {
            super();

            this.change = change;
            this.text = text;
        }

        public Change getChange()
        {
            return change;
        }

        public String getKey()
        {
            return text.substring(0, Math.min(text.length(), 1));
        }

        public String getText()
        {
            return text;
        }

        public <SELF extends Line> SELF setText(Change change, String text)
        {
            this.change = change;
            this.text = text;

            return self();
        }

        @SuppressWarnings("unchecked")
        public <SELF extends Line> SELF self()
        {
            return (SELF) this;
        }

        @Override
        public String toString()
        {
            return String.format("%8s: %s", change, text);
        }
    }

    private static class SourceLine extends Line
    {
        public SourceLine(String text)
        {
            super(text);
        }

        public SourceLine(Change change, String text)
        {
            super(change, text);
        }
    }

    private static class TargetLine extends Line
    {
        public TargetLine(String text)
        {
            super(text);
        }

        public TargetLine(Change change, String text)
        {
            super(change, text);
        }
    }

    @Test
    public void testSingleSame()
    {
        Collection<SourceLine> sourceList = createSourceList("A", "B", "C");
        Collection<TargetLine> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateSame()
    {
        Collection<SourceLine> sourceList = createSourceList("A", "A", "A", "B", "B", "B", "C", "C", "C");
        Collection<TargetLine> targetList = createTargetList("A", "A", "A", "B", "B", "B", "C", "C", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

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
        Collection<SourceLine> sourceList = createSourceList("1", "A", "2", "B", "3", "C", "4");
        Collection<TargetLine> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

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
        Collection<SourceLine> sourceList =
            createSourceList("1", "1", "A", "A", "2", "2", "B", "B", "3", "3", "C", "C", "4", "4");
        Collection<TargetLine> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

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
        Collection<SourceLine> sourceList = createSourceList("A", "B", "C");
        Collection<TargetLine> targetList = createTargetList("1", "A", "2", "B", "3", "C", "4");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateRemoves()
    {
        Collection<SourceLine> sourceList = createSourceList("A", "B", "C");
        Collection<TargetLine> targetList =
            createTargetList("1", "1", "A", "A", "2", "2", "B", "B", "3", "3", "C", "C", "4", "4");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNoNext(iterator);
    }

    @Test
    public void testSingleExchanges()
    {
        Collection<SourceLine> sourceList = createSourceList("1", "B", "C2", "D", "3");
        Collection<TargetLine> targetList = createTargetList("A", "B", "C_", "D", "E");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

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
        Collection<SourceLine> sourceList = createSourceList("1", "1", "B", "B", "C2", "C2", "D", "D", "3", "3");
        Collection<TargetLine> targetList = createTargetList("A", "A", "B", "B", "C_", "C_", "D", "D", "E", "E");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

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
        Collection<SourceLine> sourceList = createSourceList("C", "B", "A");
        Collection<TargetLine> targetList = createTargetList("A", "B", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "C");
        assertNext(iterator, Change.ADDED, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNoNext(iterator);
    }

    @Test
    public void testDuplicateReverse()
    {
        Collection<SourceLine> sourceList = createSourceList("C", "C", "B", "B", "A", "A");
        Collection<TargetLine> targetList = createTargetList("A", "A", "B", "B", "C", "C");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.ADDED, "C");
        assertNext(iterator, Change.ADDED, "C");
        assertNext(iterator, Change.ADDED, "B");
        assertNext(iterator, Change.ADDED, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "A");
        assertNoNext(iterator);
    }

    @Test
    public void testLongestSubList()
    {
        Collection<SourceLine> sourceList =
            createSourceList("@", "1", "2", "3", "A", "B", "C", "B", "A", "X", "Y", "Z", "!");
        Collection<TargetLine> targetList =
            createTargetList("@", "X", "Y", "Z", "A", "B", "C", "B", "A", "1", "2", "3", "!");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.SAME, "@");
        assertNext(iterator, Change.ADDED, "1");
        assertNext(iterator, Change.ADDED, "2");
        assertNext(iterator, Change.ADDED, "3");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "C");
        assertNext(iterator, Change.SAME, "B");
        assertNext(iterator, Change.SAME, "A");
        assertNext(iterator, Change.ADDED, "X");
        assertNext(iterator, Change.ADDED, "Y");
        assertNext(iterator, Change.ADDED, "Z");
        assertNext(iterator, Change.SAME, "!");
        assertNoNext(iterator);
    }

    @Test
    public void testUpdate()
    {
        Collection<SourceLine> sourceList = createSourceList("A1", "B2", "C3");
        Collection<TargetLine> targetList = createTargetList("A_", "B_", "C_");

        MapperUtils.mapOrdered(sourceList, targetList, MapperUtilsTest::matches, MapperUtilsTest::map);

        Iterator<TargetLine> iterator = targetList.iterator();

        assertNext(iterator, Change.UPDATED, "A1");
        assertNext(iterator, Change.UPDATED, "B2");
        assertNext(iterator, Change.UPDATED, "C3");
        assertNoNext(iterator);
    }

    protected static void assertNext(Iterator<TargetLine> iterator, Change change, String text)
    {
        TargetLine targetLine = iterator.next();

        assertThat(targetLine.getChange(), is(change));
        assertThat(targetLine.getText(), is(text));
    }

    protected static void assertNoNext(Iterator<TargetLine> iterator)
    {
        assertThat(iterator.hasNext(), is(false));
    }

    protected static boolean matches(SourceLine sourceLine, TargetLine targetLine)
    {
        return Objects.equals(sourceLine.getKey(), targetLine.getKey());
    }

    protected static TargetLine map(SourceLine sourceLine, TargetLine targetLine)
    {
        if (targetLine == null)
        {
            return new TargetLine(Change.ADDED, sourceLine.getText());
        }

        if (!Objects.equals(sourceLine.getText(), targetLine.getText()))
        {
            targetLine.setText(Change.UPDATED, sourceLine.getText());
        }

        return targetLine;
    }

    protected static Collection<SourceLine> createSourceList(String... lines)
    {
        return Arrays.stream(lines).map(SourceLine::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected static Collection<TargetLine> createTargetList(String... lines)
    {
        return Arrays.stream(lines).map(TargetLine::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
