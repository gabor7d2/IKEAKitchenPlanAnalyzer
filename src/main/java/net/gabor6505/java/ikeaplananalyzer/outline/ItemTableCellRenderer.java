package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;
import net.gabor6505.java.ikeaplananalyzer.Property;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;

import javax.swing.*;
import java.awt.*;

public class ItemTableCellRenderer extends DefaultOutlineCellRenderer {

    private final int horizontalAlignment;

    public ItemTableCellRenderer(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        label.setHorizontalAlignment(horizontalAlignment);

        if (isSelected) return label;

        label.setForeground(getForeground(value));

        return label;

    }

    private Color getForeground(Object o) {
        Color returnColor = Color.BLACK;

        if (o instanceof ItemGroup) {
            ItemGroup group = (ItemGroup) o;

            boolean hasEnabledItems = group.isEnabled();
            if (!hasEnabledItems) returnColor = ItemGroup.DISABLED_COLOR;
            else returnColor= ItemGroup.PARTIALLY_ENABLED_COLOR;

            if (group.areAllItemsEnabled()) returnColor = ItemGroup.FULLY_ENABLED_COLOR;

        } else if (o instanceof Item) {
            return ((Item)o).isEnabled() ? Item.ENABLED_COLOR : Item.DISABLED_COLOR;

        } else if (o instanceof Property) {
            Property property = (Property) o;
            return getForeground(property.getOwner());
        }

        return returnColor;
    }
}
