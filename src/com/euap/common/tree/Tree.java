package com.euap.common.tree;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class Tree implements Iterable<TreeNode> {

    // 根节点列表
    private LinkedList<TreeNode> rootList = new LinkedList<TreeNode>();
    // 所有节点 id -> node
    private Map<String, TreeNode> nodeMap = new HashMap<String, TreeNode>();
    // 缓存了尚未构建的parentId。如果增加尚未构建的parentNode，将找到的parentNode配置为更新的节点。
    private Map<String, TreeNode> parentCache = new HashMap<String, TreeNode>();

    public Tree addNode(TreeNode node, String parentId) {
        nodeMap.put(node.getId(), node);
        if (parentCache.containsKey(node.getId())) {
            node.setChildren(parentCache.get(node.getId()).getChildren());
        }
        if (StringUtils.isEmpty(parentId)) {
            rootList.add(node);
        } else {
            TreeNode TreeNode = nodeMap.get(parentId);
            if (TreeNode == null) {
                if (parentCache.containsKey(parentId)) {
                    parentCache.get(parentId).addChild(node);
                } else {
                    TreeNode p = new TreeNode();
                    p.setId(parentId);
                    p.addChild(node);
                    parentCache.put(parentId, p);
                }
            } else {
                if (!parentCache.containsKey(parentId)) {
                    parentCache.put(parentId, TreeNode);
                }
                TreeNode.addChild(node);
            }
        }
        return this;
    }

    public Iterator<TreeNode> iterator() {
        return rootList.iterator();
    }


    /**
     * 替换指定父级节点下的节点
     *
     * @param node
     * @param parentId
     */
    public void replaceNode(TreeNode node, String parentId) {
        if (!nodeMap.containsKey(node.getId()))
            return;
        if (parentCache.containsKey(node.getId())) {
            node.setChildren(parentCache.get(node.getId()).getChildren());
        }
        if (StringUtils.isEmpty(parentId)) {
            for (int i = 0; i < rootList.size(); i++) {
                TreeNode cNode = rootList.get(i);
                if (StringUtils.equals(cNode.getId(), node.getId())) {
                    rootList.set(i, node);
                    break;
                }
            }
        } else {
            if (parentCache.containsKey(parentId)) {
                TreeNode pNode = parentCache.get(parentId);
                for (int i = 0; i < pNode.getChildren().size(); i++) {
                    TreeNode cNode = pNode.getChildren().get(i);
                    if (StringUtils.equals(cNode.getId(), node.getId())) {
                        pNode.getChildren().set(i, node);
                        break;
                    }
                }
            } else {
                TreeNode p = new TreeNode();
                p.setId(parentId);
                p.addChild(node);
                parentCache.put(parentId, p);
            }
        }
    }

    public Tree clone() {
        Tree result = new Tree();
        for (Iterator<TreeNode> it = rootList.iterator(); it.hasNext(); ) {
            TreeNode node = it.next();
            result.addNode(node.cloneByTree(result), null);
        }
        return result;
    }

    public static void main(String[] args) {
        Tree m = new Tree();
        m.addNode(new TreeNode("01", "银行", true, null, null), null).addNode(new TreeNode("02", "银行", true, null, null), "0001")
                .addNode(new TreeNode("0001", "银行", true, null, null), "01");
        try {
            System.out.println("银行");
            System.out.println(new ObjectMapper().writeValueAsString(m));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
