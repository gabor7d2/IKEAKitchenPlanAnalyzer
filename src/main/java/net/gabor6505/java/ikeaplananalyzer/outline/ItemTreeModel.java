package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.Item;
import net.gabor6505.java.ikeaplananalyzer.ItemGroup;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

public class ItemTreeModel implements TreeModel {

    private final List<? extends ItemGroup> itemGroups;

    public ItemTreeModel(List<? extends ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    @Override
    public Object getRoot() {
        return "Root";
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent.equals("Root")) {
            return itemGroups.get(index);
        } else if (parent instanceof ItemGroup) {
            ItemGroup group = (ItemGroup) parent;
            return group.getItem(index);
        }
        return "error";
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals("Root")) {
            return itemGroups.size();
        } else if (parent instanceof ItemGroup) {
            ItemGroup group = (ItemGroup) parent;
            return group.getItems().size();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Item;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals("Root")) {
            return itemGroups.indexOf(child);
        } else if (parent instanceof ItemGroup) {
            ItemGroup group = (ItemGroup) parent;
            return group.getItems().indexOf(child);
        }
        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
