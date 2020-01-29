package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.Selectable;
import net.gabor6505.java.ikeaplananalyzer.helper.SelectionListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item implements Selectable {

    public final static Color ENABLED_COLOR = Color.BLACK;
    public final static Color DISABLED_COLOR = Color.LIGHT_GRAY;

    public final static String INVALID_ID = "00000000";

    private final SelectionListener listener;
    private final ItemGroup group;

    private final Property<Item, ItemCategory> itemCategory = new Property<>(this, "itemCategory");

    private final Property<Item, String> id = new Property<Item, String>(this, "id") {
        @Override
        public String toString() {
            return getValue().equals(INVALID_ID) ? "" : getValue();
        }
    };
    private final Property<Item, String> familyName = new Property<>(this, "familyName");
    private final Property<Item, String> name = new Property<>(this, "name");
    private final Property<Item, String> appearance = new Property<>(this, "appearance");
    private final Property<Item, String> size = new Property<>(this, "size");

    private final Property<Item, Double> price = new Property<>(this, "price", -1.0);
    private final Property<Item, Integer> quantity = new Property<>(this, "quantity", -1);

    private boolean enabled = true, locked = false;

    public Item(SelectionListener listener, ItemGroup group) {
        this.listener = listener;
        this.group = group;
    }

    public void addDetail(String str) {
        processDetail(str);
    }

    private void processDetail(String str) {
        str = str.trim();

        // Extracting size
        if (str.endsWith(" cm") || str.endsWith(" mm") || str.endsWith(" m") || str.endsWith(" °") || str.endsWith(" \"")) {
            size.setValue(str);
            return;
        }

        // Extracting price and quantity
        if (!Main.fileIsEnglish) {
            Matcher m = Pattern.compile("(?:.* )?([0-9.]+) Ft ([0-9]+) [0-9.]+ Ft.*").matcher(str);
            if (m.matches()) {
                price.setValue(Integer.valueOf(m.group(1).replaceAll("\\.", "")).doubleValue());
                quantity.setValue(Integer.valueOf(m.group(2)));
                str = str.replaceFirst("([0-9.]+) Ft ([0-9]+) [0-9.]+ Ft", "");
            }
        } else {
            Matcher m = Pattern.compile("(?:.* )?[$£]([0-9.]+) ([0-9]+) [$£][0-9.]+.*").matcher(str);
            if (m.matches()) {
                price.setValue(Double.valueOf(m.group(1)));
                quantity.setValue(Integer.valueOf(m.group(2)));
                str = str.replaceFirst("[$£]([0-9.]+) ([0-9]+) [$£][0-9.]+", "");
            }
        }

        str = str.trim();

        // Extracting id, familyName and name
        Matcher m2 = Pattern.compile("([0-9]{8}) ([A-ZÁ-ÜŰŐ ]+) (.+)").matcher(str);
        if (m2.matches()) {
            String[] substrings = str.split(" ");
            id.setValue(substrings[0]);
            familyName.setValue(substrings[1]);

            StringBuilder nameBuilder = new StringBuilder();
            List<String> validNameStrings = new ArrayList<>();

            for (int i = 2; i < substrings.length; i++) {
                substrings[i] = substrings[i].trim();
                if (substrings[i].isEmpty()) continue;

                validNameStrings.add(substrings[i]);
            }

            for (int i = 0; i < validNameStrings.size(); i++) {
                nameBuilder.append(validNameStrings.get(i));
                if (i != validNameStrings.size() - 1) nameBuilder.append(" ");
            }

            name.setValue(nameBuilder.toString());
            itemCategory.setValue(ItemCategory.getCategory(name.getValue()));
            if (group != null && name.getValue() != null && group.getItems().size() == 1) {
                group.setFurnitureCategory(FurnitureCategory.getCategory(name.getValue()));
            }
        }

        // Extracting appearance
        Matcher m3 = Pattern.compile("[a-zá-üűő ]+").matcher(str);
        if (m3.matches()) {
            appearance.setValue(str);
        }
    }

    public ItemCategory getItemCategory() {
        return itemCategory.getValue();
    }

    public String getId() {
        if (id.getValue() == null) return null;
        return id.getValue().equals(INVALID_ID) ? "" : id.getValue();
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public String getFamilyName() {
        return familyName.getValue();
    }

    public void setFamilyName(String familyName) {
        this.familyName.setValue(familyName);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public String getAppearance() {
        return appearance.getValue();
    }

    public void setAppearance(String appearance) {
        this.appearance.setValue(appearance);
    }

    public String getSize() {
        return size.getValue();
    }

    public void setSize(String size) {
        this.size.setValue(size);
    }

    public double getPrice() {
        return price.getValue();
    }

    public void setPrice(double price) {
        this.price.setValue(price);
    }

    public int getQuantity() {
        return quantity.getValue();
    }

    public void setQuantity(int quantity) {
        this.quantity.setValue(quantity);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        if (locked) return;
        enabled = value;
        listener.SelectionChanged(this, value);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns the requested property, or null if a property with the specified name doesn't exist
     *
     * @param name The name of the property
     * @return The property corresponding to the specified name or null if there is no
     * property with this name
     */
    public Property getProperty(String name) {
        switch (name) {
            case "itemCategory":
                return itemCategory;
            case "id":
                return id;
            case "familyName":
                return familyName;
            case "name":
                return this.name;
            case "appearance":
                return appearance;
            case "size":
                return size;
            case "price":
                return price;
            case "quantity":
                return quantity;
        }
        return null;
    }

    @Override
    public String toString() {
        if (getFamilyName() == null || getFamilyName().isEmpty()) return getName();
        return getFamilyName() + " " + getName();
    }
}
