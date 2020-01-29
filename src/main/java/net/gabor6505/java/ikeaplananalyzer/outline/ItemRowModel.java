package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;
import net.gabor6505.java.ikeaplananalyzer.Main;
import net.gabor6505.java.ikeaplananalyzer.Property;
import org.netbeans.swing.outline.RowModel;

import java.text.NumberFormat;

public class ItemRowModel implements RowModel {

    private final static String[] groupViewNames = {"category", "size", "appearance", "id", "quantity", "price"};
    private final static String[] categoryViewNames = {"size", "appearance", "id", "quantity", "price"};

    private final String[] columnNames;
    private boolean categoryView = false;

    public ItemRowModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public ItemRowModel(String[] columnNames, boolean categoryView) {
        this(columnNames);
        this.categoryView = categoryView;
    }

    @Override
    public int getColumnCount() {
        return categoryView ? categoryViewNames.length : groupViewNames.length;
    }

    @Override
    public Object getValueFor(Object o, int i) {
        String[] viewNames = categoryView ? categoryViewNames : groupViewNames;

        switch (viewNames[i]) {
            case "category":
                if (o instanceof ItemGroup) {
                    /*ItemGroup group = (ItemGroup) o;
                    return group.getFurnitureCategoryName();*/
                    return "";
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    return new Property<>(item, "itemCategoryName", item.getItemCategory().getDisplayName());
                }
                break;
            case "size":
                if (o instanceof ItemGroup) {
                    return "";
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    return item.getSize() != null ? item.getProperty("size") : "";
                }
                break;
            case "appearance":
                if (o instanceof ItemGroup) {
                    return "";
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    return item.getAppearance() != null ? item.getProperty("appearance") : "";
                }
                break;
            case "id":
                if (o instanceof ItemGroup) {
                    return "";
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    return item.getId() != null ? item.getProperty("id") : "";
                }
                break;
            case "quantity":
                if (o instanceof ItemGroup) {
                    return "";
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    return item.getProperty("quantity");
                }
                break;
            case "price":
                if (o instanceof ItemGroup) {
                    ItemGroup group = (ItemGroup) o;

                    int price = 0;
                    for (Item item : group.getItems()) {
                        if (!item.isEnabled()) continue;
                        if (item.getPrice() > 0 && item.getQuantity() > 0)
                            price += item.getPrice() * item.getQuantity();
                    }

                    String displayPrice = Main.formatPrice(price);
                    return new Property<>(group, "groupDisplayPrice", displayPrice);
                } else if (o instanceof Item) {
                    Item item = (Item) o;
                    String displayPrice = item.getPrice() > 0 && item.getQuantity() > 0 ? getDisplayPrice(item) : "";
                    return new Property<>(item, "itemDisplayPrice", displayPrice);
                }
                break;
        }
        return "error";
    }

    @Override
    public Class getColumnClass(int i) {
        return String.class;
    }

    @Override
    public String getColumnName(int i) {
        return columnNames[i];
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    @Override
    public void setValueFor(Object o, int i, Object o1) {

    }

    private String getDisplayPrice(Item item) {
        /*return formatter.format(item.getPrice()) + " Ft x " + item.getQuantity() + " = " +
                formatter.format(item.getPrice() * item.getQuantity()) + " Ft";*/
        return Main.formatPrice(item.getPrice() * item.getQuantity());
    }
}
