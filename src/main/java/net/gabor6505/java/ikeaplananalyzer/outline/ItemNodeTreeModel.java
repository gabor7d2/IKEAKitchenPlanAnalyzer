package net.gabor6505.java.ikeaplananalyzer.outline;

import net.gabor6505.java.ikeaplananalyzer.ItemGroup;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class ItemNodeTreeModel extends ItemTreeModel {

    public ItemNodeTreeModel(List<? extends ItemGroup> itemGroups) {
        super(itemGroups);
    }

    @Override
    public Object getRoot() {
        return new DefaultMutableTreeNode(super.getRoot());
    }

    @Override
    public Object getChild(Object parent, int index) {
        return new DefaultMutableTreeNode(super.getChild(((DefaultMutableTreeNode) parent).getUserObject(), index));
    }

    @Override
    public int getChildCount(Object parent) {
        return super.getChildCount(((DefaultMutableTreeNode) parent).getUserObject());
    }

    @Override
    public boolean isLeaf(Object node) {
        return super.isLeaf(((DefaultMutableTreeNode) node).getUserObject());
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return super.getIndexOfChild(((DefaultMutableTreeNode) parent).getUserObject(), ((DefaultMutableTreeNode) child).getUserObject());
    }
}
