package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.Main;
import net.gabor6505.java.ikeaplananalyzer.Property;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.EventObject;

public class CustomOutline extends Outline {

    public CustomOutline() {
        super();
    }

    public CustomOutline(OutlineModel mdl) {
        super(mdl);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (e instanceof MouseEvent && ((MouseEvent) e).getClickCount() > 1) {
            Object o = getValueAt(row, column);
            Item item = null;
            if (o instanceof Property && ((Property) o).getOwner() instanceof Item) {
                item = (Item) ((Property) o).getOwner();
            } else if (o instanceof Item) {
                item = (Item) o;
            }
            if (item != null) openProductSite(item);
        }
        return super.editCellAt(row, column, e);
    }

    private void openProductSite(Item item) {
        if (item.getId().length() < 1 || !item.getId().matches("[0-9]{8}")) return;

        String countryFlag = "hu/hu";
        if (Main.fileIsEnglish) {
            switch (Main.currencySymbol) {
                case '$':
                    countryFlag = "us/en";
                    break;
                case '£':
                    countryFlag = "gb/en";
                    break;
            }
        }

        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(String.format("https://www.ikea.com/%s/catalog/products/%s", countryFlag, item.getId())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

