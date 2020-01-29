package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.IGroupIterator;

import java.util.ArrayList;
import java.util.List;

public class ItemCategoryGroup extends ItemGroup {

    public final static List<ItemCategoryGroup> ITEM_CATEGORY_GROUPS = new ArrayList<>();

    public ItemCategoryGroup(int id) {
        super(id, false);
        ITEM_CATEGORY_GROUPS.add(this);
    }

    @Override
    public String toString() {
        return getId() + 1 + " - " + getFurnitureCategoryName();
    }

    public static void iterate(IGroupIterator iterator) {
        iterate(ITEM_CATEGORY_GROUPS, iterator);
    }
}
