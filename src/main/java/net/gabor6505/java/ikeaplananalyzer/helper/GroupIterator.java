package net.gabor6505.java.ikeaplananalyzer.helper;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;

public abstract class GroupIterator implements IGroupIterator {

    @Override
    public void atItem(ItemGroup group, Item currentItem, int groupIndex, int itemIndex) {

    }

    @Override
    public void beforeGroup(ItemGroup group, int groupIndex) {

    }

    @Override
    public void afterGroup(ItemGroup group, int groupIndex) {

    }
}
