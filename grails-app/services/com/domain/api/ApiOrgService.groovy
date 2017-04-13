package com.domain.api

import com.domain.auth.User
import com.util.ExtTreePanel
import com.util.TreeUtil
import com.utils.Constants
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Transactional
class ApiOrgService {

    def serviceMethod() {

    }

    def treePanel() {
        ExtTreePanel extTreePanel = new ExtTreePanel("ApiOrg", "/apiOrg/tree")
        extTreePanel.addColumnData("apiOrg.userApiOrg.label", Constants.DATATYPE_STRING, 20, false)
        extTreePanel.addColumnData("apiOrg.id.label", Constants.DATATYPE_STRING, 10, false)
        extTreePanel.addColumnData("apiOrg.text.label", Constants.DATATYPE_STRING, 200, true)
        extTreePanel.addColumnData("apiOrg.method.label", Constants.DATATYPE_STRING, 100, true)
        return extTreePanel
    }

    def tree() {
        def listOfOrgs = ApiOrg.findAll()
        TreeUtil tu = new TreeUtil();

        //根据组织层级，增加所有节点
        for (ApiOrg org : listOfOrgs) {
            String hierarchy = org.getHierarchy()
            String orgName = org.getName()
            String method = org.getMethod()
            Long id = org.getId()
            String parent = hierarchy.substring(0, hierarchy.length() - 2);
            if (parent.length() == 0) parent = TreeUtil.ROOT_ID;

            Map nodeMap = new HashMap();
            nodeMap.put("method", method);
            nodeMap.put("userApiOrg", "");
            nodeMap.put("id", id);
            nodeMap.put("expanded", true);
            tu.AddNode(parent, hierarchy, orgName, nodeMap);
        }
        return tu.GetJsonTree()
    }

    def findZTreeJSON(Long userId) {
        def apiOrgs = finAllByUser(User.load(userId))
        JSONArray moduleArray = new JSONArray()
        apiOrgs?.each {
            String hierarchy = it.getHierarchy()
            String parent = hierarchy.substring(0, hierarchy.length() - 2);
            Long pid;
            if (parent.length() == 0) {
                pid = 0;
            } else {
                pid = ApiOrg.findByHierarchy(parent).id;
            }
            def childObj = new JSONObject()
            if (it.type == 0 || it.type == 1) {
                childObj.put('click', false)
            }
            childObj.put('name', it.name)
            childObj.put("open", true)
            childObj.put('pId', pid)
            childObj.put('id', it.id)
//            childObj.put('url', "/wx/show/" + it.id)
            childObj.put('target', "_self")
            moduleArray.put(childObj)
        }

        return moduleArray

    }

    def finAllByUser(User user) {
        def apiOrgs = UserApiOrg.findAllByUser(user).apiOrg
        StringBuilder whereCase = new StringBuilder()
        if (apiOrgs && apiOrgs.size() > 0) {
            apiOrgs?.eachWithIndex { apiOrg, index ->
                whereCase.append(" a.hierarchy like ").append("'").append(apiOrg.hierarchy).append("%' ")
                if (index < apiOrgs.size() - 1) {
                    whereCase.append(" or ")
                }
            }
        } else {
            whereCase.append(" 1 = 2 ")
        }
        log.info(whereCase.toString())
        def queryStr = "select  a from ApiOrg as a where " + whereCase.toString()
        log.info(queryStr)
        return ApiOrg.executeQuery(queryStr)
    }

    def delete(ApiOrg apiOrg) {
        UserApiOrg.where {
            apiOrg == apiOrg
        }.deleteAll()
        apiOrg.delete(flush: true)

    }


}
