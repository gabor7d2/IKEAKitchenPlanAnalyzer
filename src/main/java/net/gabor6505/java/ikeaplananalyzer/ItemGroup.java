package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.IGroupIterator;
import net.gabor6505.java.ikeaplananalyzer.helper.Selectable;
import net.gabor6505.java.ikeaplananalyzer.helper.SelectionListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemGroup implements Selectable {

    public final static List<ItemGroup> ITEM_GROUPS = new ArrayList<>();

    public final static Color FULLY_ENABLED_COLOR = Color.BLACK;
    public final static Color PARTIALLY_ENABLED_COLOR = new Color(96, 96, 96);
    public final static Color DISABLED_COLOR = Color.LIGHT_GRAY;

    private int id;

    private final List<Item> items = new ArrayList<>();

    private FurnitureCategory furnitureCategory = FurnitureCategory.get("other");

    public ItemGroup(int id) {
        this.id = id;
        ITEM_GROUPS.add(this);
    }

    public ItemGroup(int id, boolean addToGroup) {
        this.id = id;
        if (addToGroup) ITEM_GROUPS.add(this);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> newItems) {
        items.clear();
        items.addAll(newItems);
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    /**
     * Returns true if this group has at least 1 enabled item;
     */
    public boolean isEnabled() {
        for (Item i : items) {
            if (i.isEnabled()) return true;
        }
        return false;
    }

    /**
     * Returns true if and only if all of the items in this group are enabled
     */
    public boolean areAllItemsEnabled() {
        for (Item i : items) {
            if (!i.isEnabled()) return false;
        }
        return true;
    }

    public void setEnabled(boolean value) {
        for (Item i : items) {
            i.setEnabled(value);
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item addNewItem(SelectionListener notifier) {
        Item item = new Item(notifier, this);
        items.add(item);
        return item;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FurnitureCategory getFurnitureCategory() {
        return furnitureCategory;
    }

    public void setFurnitureCategory(FurnitureCategory furnitureCategory) {
        this.furnitureCategory = furnitureCategory;
    }

    public String getFurnitureCategoryName() {
        if (furnitureCategory != null && furnitureCategory != FurnitureCategory.get("other")) {
            return furnitureCategory.getDisplayName();
        } else {
            return items.size() > 0 ? items.get(0).getItemCategory().getDisplayName() : FurnitureCategory.get("other").getDisplayName();
        }
    }

    @Override
    public String toString() {
        return getId() + 1 + " - " + getFurnitureCategoryName();
    }

    public static void iterate(IGroupIterator iterator) {
        iterate(ITEM_GROUPS, iterator);
    }

    public static void iterate(List<? extends ItemGroup> groupList, IGroupIterator iterator) {
        for (int i = 0; i < groupList.size(); i++) {
            ItemGroup group = groupList.get(i);
            iterator.beforeGroup(group, i);

            for (int j = 0; j < group.getItems().size(); j++) {
                Item item = group.getItems().get(j);
                iterator.atItem(group, item, i, j);
            }
            iterator.afterGroup(group, i);
        }
    }
}
