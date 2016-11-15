package at.porscheinformatik.antimapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Assert;

public abstract class AbstractMapperUtilsTest
{

    protected enum Change
    {
        SAME,
        ADDED,
        UPDATED
    };

    protected abstract static class Item
    {
        private Change change;
        private String text;

        public Item(String text)
        {
            this(Change.SAME, text);
        }

        public Item(Change change, String text)
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
            return toKey(text);
        }

        public String getText()
        {
            return text;
        }

        public <SELF extends Item> SELF setText(Change change, String text)
        {
            this.change = change;
            this.text = text;

            return self();
        }

        @SuppressWarnings("unchecked")
        public <SELF extends Item> SELF self()
        {
            return (SELF) this;
        }

        @Override
        public String toString()
        {
            return String.format("%8s: %s", change, text);
        }
    }

    protected static class SourceItem extends Item
    {
        public SourceItem(String text)
        {
            super(text);
        }

        public SourceItem(Change change, String text)
        {
            super(change, text);
        }
    }

    protected static class TargetItem extends Item
    {
        public TargetItem(String text)
        {
            super(text);
        }

        public TargetItem(Change change, String text)
        {
            super(change, text);
        }
    }

    protected void createRandomItems(List<String> sourceItems, List<String> targetItems, int count)
    {
        char ch = 'A';

        for (int i = 0; i < count; i++)
        {
            targetItems.add(String.valueOf(ch));

            if (Math.random() < 0.5)
            {
                ch++;
            }
        }

        Collections.shuffle(targetItems);

        count = Math.max(count, (int) (Math.random() * 8));

        Iterator<String> iterator = targetItems.iterator();

        while (iterator.hasNext())
        {
            String targetItem = iterator.next();

            if ("B".equals(toKey(targetItem)))
            {
                sourceItems.add("!");
                iterator.remove();
            }
            else
            {
                sourceItems.add(targetItem);
            }
        }

        while (sourceItems.size() < count)
        {
            int pos = (int) (Math.random() * (sourceItems.size() - 1));

            sourceItems.add(pos, String.valueOf(ch));

            if (Math.random() < 0.5)
            {
                ch++;
            }
        }

        int i = 0;

        while (i < sourceItems.size())
        {
            double wat = Math.random();

            if (wat < 0.25)
            {
                sourceItems.remove(i);
            }
            else if (wat < 0.5)
            {
                int pos = (int) (Math.random() * (sourceItems.size() - 1));

                sourceItems.add(pos, sourceItems.remove(i));
            }
            else
            {

                i++;
            }
        }
    }

    protected static int countNotNull(Collection<String> collection)
    {
        int i = 0;

        for (String item : collection)
        {
            if (item != null && !"!".equals(toKey(item)))
            {
                i++;
            }
        }

        return i;
    }

    protected <AnyItem extends Item> AnyItem assertAny(Collection<AnyItem> collection, Change change, String text)
    {
        String key = toKey(text);

        for (AnyItem item : collection)
        {
            if (item == null && key == null)
            {
                return item;
            }

            if (item != null && Objects.equals(key, item.getKey()))
            {
                if (change != null)
                {
                    assertThat(item.getChange(), is(change));
                }

                assertThat(item.getText(), is(text));

                return item;
            }
        }

        Assert.fail("No item with key: " + key);

        return null;
    }

    protected void assertNo(Collection<? extends Item> collection, String text)
    {
        String key = toKey(text);

        for (Item item : collection)
        {
            if (item == null && key == null)
            {
                Assert.fail("Collection contains item with key: " + key);
            }

            if (item != null && Objects.equals(key, item.getKey()))
            {
                Assert.fail("Collection contains item with key: " + key);
            }
        }
    }

    protected static void assertNext(Iterator<TargetItem> iterator, Change change, String text)
    {
        if (!iterator.hasNext())
        {
            Assert.fail("No next");
        }

        TargetItem targetItem = iterator.next();

        assertThat(targetItem.getChange(), is(change));
        assertThat(targetItem.getText(), is(text));
    }

    protected static void assertNoNext(Iterator<TargetItem> iterator)
    {
        assertThat(iterator.hasNext(), is(false));
    }

    protected static boolean nullFilter(Object item)
    {
        return item != null;
    }

    protected static boolean matches(SourceItem sourceItem, TargetItem targetItem)
    {
        return Objects.equals(sourceItem.getKey(), targetItem.getKey());
    }

    protected static TargetItem map(SourceItem sourceItem, TargetItem targetItem)
    {
        if (sourceItem == null)
        {
            return null;
        }

        if ("!".equals(sourceItem.getKey()))
        {
            return null;
        }

        if (targetItem == null)
        {
            return new TargetItem(Change.ADDED, sourceItem.getText());
        }

        if (!Objects.equals(sourceItem.getText(), targetItem.getText()))
        {
            targetItem.setText(Change.UPDATED, sourceItem.getText());
        }

        return targetItem;
    }

    protected static String toKey(String text)
    {
        if (text == null)
        {
            return null;
        }

        return text.substring(0, Math.min(text.length(), 1));
    }

    protected static Collection<SourceItem> createSourceList(String... items)
    {
        return Arrays.stream(items).map(SourceItem::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected static Collection<TargetItem> createTargetList(String... items)
    {
        return Arrays.stream(items).map(TargetItem::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
