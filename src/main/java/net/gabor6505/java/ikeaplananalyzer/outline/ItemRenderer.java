package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;
import net.gabor6505.java.ikeaplananalyzer.Main;
import net.gabor6505.java.ikeaplananalyzer.Property;
import net.gabor6505.java.ikeaplananalyzer.helper.Selectable;
import org.netbeans.swing.outline.CheckRenderDataProvider;
import org.netbeans.swing.outline.Outline;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;

public class ItemRenderer implements CheckRenderDataProvider {

    private static Icon LEAF_ICON;
    private final Outline parentOutline;

    static {
        try {
            LEAF_ICON = new ImageIcon(ImageIO.read(ItemRenderer.class.getResourceAsStream("/leaf_icon_16.png")));
        } catch (IOException e) {
            e.printStackTrace();
            LEAF_ICON = null;
        }
    }

    public ItemRenderer(Outline parentOutline) {
        this.parentOutline = parentOutline;
    }

    @Override
    public String getDisplayName(Object o) {
        return null;
    }

    @Override
    public boolean isHtmlDisplayName(Object o) {
        return false;
    }

    @Override
    public Color getBackground(Object o) {
        return null;
    }

    @Override
    public Color getForeground(Object o) {
        return null;
    }

    @Override
    public String getTooltipText(Object o) {
        if (o instanceof Property && ((Property) o).getName().equals("itemDisplayPrice")) {
            Item source = (Item) ((Property) o).getOwner();
            return source.getQuantity() + " x " + Main.formatPrice(source.getPrice());
        }
        return null;
    }

    @Override
    public Icon getIcon(Object o) {
        return (o instanceof Item && System.getProperty("os.name").toLowerCase().contains("win") && LEAF_ICON != null) ? LEAF_ICON : null;
    }

    @Override
    public boolean isCheckable(Object o) {
        return o instanceof Selectable;
    }

    @Override
    public boolean isCheckEnabled(Object o) {
        return o instanceof Selectable;
    }

    @Override
    public Boolean isSelected(Object o) {
        if (o instanceof Selectable) {
            return ((Selectable) o).isEnabled();
        }
        return null;
    }

    @Override
    public void setSelected(Object o, Boolean state) {
        if (o instanceof Selectable) {
            ((Selectable) o).setEnabled(state);
        }
        parentOutline.repaint();
    }
}
