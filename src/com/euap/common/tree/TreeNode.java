package com.euap.common.tree;

/**
 * Created by Administrator on 2017/9/21 0021.
 */


import httl.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;


public class TreeNode {

    private String id;
    private String text;
    private boolean leaf = true;
    private String iconCls;
    private boolean expanded = false;
    private List<TreeNode> children;
    private String qtip;

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    private Object extraData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<TreeNode> getChildren() {
        return children;
    }



    public String getQtip() {
        return qtip;
    }

    public void setQtip(String qtip) {
        this.qtip = qtip;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
        if (children != null) {
            this.leaf = false;
        }
    }

    public TreeNode addChild(TreeNode node) {
        if (children == null) {
            this.children = new LinkedList<TreeNode>();
            this.leaf = false;
        }
        children.add(node);
        return this;
    }

    public TreeNode(String id, String text, boolean leaf, String iconCls, List<TreeNode> children) {
        super();
        this.id = id;
        this.text = text;
        this.leaf = leaf;
        this.iconCls = iconCls;
        this.children = children;
    }

    public TreeNode() {
        super();
    }

    @Override
    public String toString() {
        return "TreeNode [id=" + id + ", text=" + text + ", leaf=" + leaf + ", iconCls=" + iconCls + ", children=" + children + "]";
    }

    public TreeNode clone() {
        TreeNode result = new TreeNode();
        result.setId(id);
        result.setText(text);
        result.setLeaf(leaf);
        result.setIconCls(iconCls);
        result.setExpanded(expanded);
        for (TreeNode node : this.children) {
            result.addChild(node.clone());
        }
        return result;
    }

    public TreeNode cloneByTree(Tree model) {
        TreeNode result = new TreeNode();
        result.setId(id);
        result.setText(text);
        result.setLeaf(leaf);
        result.setIconCls(iconCls);
        result.setExpanded(expanded);
        if (CollectionUtils.isNotEmpty(this.children))
            for (TreeNode node : this.children) {
                model.addNode(node.cloneByTree(model), this.id);
            }
        return result;
    }
}
