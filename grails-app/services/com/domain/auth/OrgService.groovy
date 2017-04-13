package com.domain.auth

import com.util.ExtTreePanel
import com.util.TreeUtil
import com.utils.Constants
import grails.transaction.Transactional

@Transactional
class OrgService {

    def serviceMethod() {

    }
    def treePanel() {
        ExtTreePanel extTreePanel = new ExtTreePanel("ApiOrg", "/org/tree")
        extTreePanel.addColumnData("org.userOrg.label", Constants.DATATYPE_STRING, 20, false)
        extTreePanel.addColumnData("org.id.label", Constants.DATATYPE_STRING, 10, false)
        extTreePanel.addColumnData("org.text.label", Constants.DATATYPE_STRING, 200, true)
        extTreePanel.addColumnData("org.code.label", Constants.DATATYPE_STRING, 100, true)
        return extTreePanel
    }
    def tree() {
        def listOfOrgs = Org.findAll()
        TreeUtil tu = new TreeUtil();

        //根据组织层级，增加所有节点
        for (Org org : listOfOrgs) {
            String hierarchy = org.getHierarchy()
            String orgName = org.getName()
            String code = org.getCode()
            Long id = org.getId()
            String parent = hierarchy.substring(0, hierarchy.length() - 2);
            if (parent.length() == 0) parent = TreeUtil.ROOT_ID;

            Map nodeMap = new HashMap();
            nodeMap.put("code", code);
            nodeMap.put("userOrg", "");
            nodeMap.put("id", id);
            nodeMap.put("expanded", true);
            tu.AddNode(parent, hierarchy, orgName, nodeMap);
        }
        return tu.GetJsonTree()
    }

    def getOrgTree() {
        def listOfOrgs = Org.findAll()
        TreeUtil tu = new TreeUtil();

        //根据组织层级，增加所有节点
        for (Org org : listOfOrgs) {
            String hierarchy = org.getHierarchy()
            String orgName = org.getName()
            String orgCode = org.getCode()
            Long id = org.getId()
            String parent = hierarchy.substring(0, hierarchy.length() - 2);
            if (parent.length() == 0) parent = TreeUtil.ROOT_ID;

            Map nodeMap = new HashMap();
            nodeMap.put("code", orgCode);
            nodeMap.put("userOrg", "");
            nodeMap.put("id", id);
            nodeMap.put("Org_hierarchy", hierarchy);
            tu.AddNode(parent, hierarchy, orgName, nodeMap);
        }
        return tu.GetJsonTree()
    }


}
