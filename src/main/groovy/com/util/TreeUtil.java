package com.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TreeUtil {
    public final static String ROOT_ID = "0";
    public final static String ROOT_TEXT = ".";


    private Map<String, List<TreeNode>> allNodes = new HashMap<String, List<TreeNode>>();

    //Add tree node
    public void AddNode(String parentId, String id, String text, Map action) {
        List<TreeNode> nodes = allNodes.computeIfAbsent(parentId, k -> new ArrayList<TreeNode>());

        TreeNode tn = new TreeNode();
        tn.id = id;
        tn.text = text;
        tn.action = action;

        nodes.add(tn);
    }

    //Add tree node whith whether is a leaf
    // leaf -- "true" or "false"
    public void AddNodeLeafOrNot(String parentId, String id, String text, String leaf) {
        List<TreeNode> nodes = allNodes.computeIfAbsent(parentId, k -> new ArrayList<TreeNode>());

        TreeNode tn = new TreeNode();
        tn.id = id;
        tn.text = text;
        tn.leaf = leaf;

        nodes.add(tn);
    }

    public String GetJsonTree() {
//         return "{root:{expanded:true,children:[" + GetNodeJsonString(ROOT_ID) + "]}}";
        String PARENT_ID = "0";
        if (PARENT_ID.equals(ROOT_ID)) {
            return "{text:'.',children:[" + GetNodeJsonString(ROOT_ID) + "]}";
        } else {
            return "{success:true, children:[" + GetNodeJsonString(PARENT_ID) + "]}";
        }
    }

    private String GetNodeJsonString(String parentId) {
        List<String> strList = new ArrayList<String>();
        //Get All  parent Id's children
        List<TreeNode> nodes = allNodes.get(parentId);
        boolean isLeaf = false;

        //Deal all children
        if (nodes != null) {
            for (TreeNode treeNode : nodes) {
                List<TreeNode> children = allNodes.get(treeNode.id);

                isLeaf = treeNode.leaf == null && children == null || treeNode.leaf != null && treeNode.leaf.equals("true");

                if (isLeaf) {
                    //it is a leaf
                    strList.add("{text:'" + treeNode.text + "',id:'" + treeNode.id + "'," + treeNode.ResolveAction() + "leaf:true}");
                } else {
                    //it has children
                    strList.add("{text:'" + treeNode.text + "',id:'" + treeNode.id + "'," + treeNode.ResolveAction() + "children:[" + GetNodeJsonString(treeNode.id) + "]}");
                }
            }
            return StringUtils.join(strList.iterator(), ",");
        } else {
            return "";
        }


    }

    class TreeNode {
        public String id;
        public String text;
        public String leaf = null;
        public Map action = null;

        String ResolveAction() {
            StringBuilder buf = new StringBuilder();
            if (action != null) {
                for (Object o : action.keySet()) {
                    String key = o.toString();
                    String val = action.get(key).toString();
                    buf.append(key).append(":");
                    buf.append("'").append(val).append("',");
                }
            }
            return buf.toString();
        }
    }
}
