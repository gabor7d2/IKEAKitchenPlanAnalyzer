package net.gabor6505.java.ikeaplananalyzer.helper;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;

public interface IGroupIterator {

    /**
     * This method is called for each item
     *
     * @param group The group which contains the item
     * @param currentItem The current item
     * @param groupIndex The index of the current group the item is in
     * @param itemIndex The index of the current item in the current group
     */
    void atItem(ItemGroup group, Item currentItem, int groupIndex, int itemIndex);

    /**
     * This method is called before a group has been iterated through
     *
     * @param group The group which is just about to be iterated through
     * @param groupIndex The index of the group which is just about to be iterated through
     */
    void beforeGroup(ItemGroup group, int groupIndex);

    /**
     * This method is called after a group has been iterated through
     *
     * @param group The group which just finished iterating
     * @param groupIndex The index of the group which just finished iterating
     */
    void afterGroup(ItemGroup group, int groupIndex);
}
