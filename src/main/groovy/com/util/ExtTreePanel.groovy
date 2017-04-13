package com.util

import com.utils.Constants
import com.utils.MyStringUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

class ExtTreePanel {
    private List<String> arrHead = new ArrayList<>()
    private List<String> arrAttribute = new ArrayList<>()
    private List<String> arrDataAttr = new ArrayList<>()
    private List<String[]> arrSqlDataAttr = new ArrayList<>()
    private List<String> arrType = new ArrayList<>()
    private List<Boolean> arrUnVisible = new ArrayList<>()
    private List<Integer> arrWidth = new ArrayList<>()
    private Map<String, String> mapDataAttr = new HashMap<>()
    private List datas = new ArrayList()
    int totalCount

    void setDatas(List datas) {
        this.datas = datas
    }

    int getTotalCount() {
        return totalCount
    }


    void setTotalCount(int totalCount) {
        this.totalCount = totalCount
    }

    JSONObject getDatas() {
        JSONObject jsonObject = new JSONObject()
        JSONArray allDatas = new JSONArray();
        datas?.eachWithIndex { data, index ->
            def oneData = new JSONObject()
            arrAttribute?.each { arr ->
                def field = MyStringUtils.getFiledByLabel(arr)
                if (field) {
                    oneData.put(MyStringUtils.toDataIndex(arr), data[field])
                }
            }
            allDatas.put(oneData)
        }
        jsonObject.put("datas", allDatas)
        jsonObject.put("totalCount", this.totalCount)
        return jsonObject
    }

    JSONArray getFields() {
        def jSONArray = new JSONArray();
        arrAttribute?.each {
            jSONArray.put(MyStringUtils.getFiledByLabel(it))
        }
        return jSONArray
    }

    JSONArray getColumns() {
        JSONArray columns = new JSONArray()
        if (hasRowNumber) {
            JSONObject rowNumberColumn = new JSONObject()
            rowNumberColumn.put("xtype", "rownumberer")
            rowNumberColumn.put("width", 40)
            columns.put(rowNumberColumn)
        }
        for (int i = 0; i < arrHead.size(); i++) {
            JSONObject oneColumn = new JSONObject()
            oneColumn.put("text", arrHead.get(i))
            String attr = arrAttribute.get(i)
            oneColumn.put("dataIndex", MyStringUtils.getFiledByLabel(attr))
            oneColumn.put(isFlex ? "flex" : "width", arrWidth.get(i))
            def field = MyStringUtils.getFiledByLabel(attr)

            if (Objects.equals(attr, "lockedColumn")) {
                oneColumn.put("locked", true)
            }
            if (attr.equals("checkbox")) {
                oneColumn.put("xtype", "checkcolumn")
            }

            oneColumn.put("hidden", !arrUnVisible.get(i))
            String type = arrType.get(i)
            if (type.equals(Constants.DATATYPE_DOUBLE) || type.equals(Constants.DATATYPE_INGEGER) || type.equals(Constants.DATATYPE_LONG)) {
                oneColumn.put("align", "right")
                oneColumn.put("xtype", "numbercolumn")
                if (type.equals(Constants.DATATYPE_DOUBLE)) {
                    oneColumn.put("'format'", "0,000.00")
                } else {
                    oneColumn.put("'format'", "0,000")
                }
            }
            if (field == "text") {
                oneColumn.put("xtype", "treecolumn")
            }
            columns.put(oneColumn)
        }
        return columns

    }

    private boolean hasRowNumber
    private boolean isFlex = true
    private String url
    private String name
    private String createUrl
    private String renderTo

    ExtTreePanel() {

    }


    ExtTreePanel(String name, String url) {
        this.name = name
        this.url = url
    }

    String getHeader() {
        return new JSONArray(arrHead).toString()
    }

    void addColumnData(String head, String attributeName, String type, int colWidth, boolean visible) {
        arrHead.add(head)
        arrAttribute.add(attributeName)
        arrType.add(type)
        arrWidth.add(colWidth)
        arrUnVisible.add(visible)

        arrDataAttr.add(attributeName)
        arrSqlDataAttr.add([attributeName, type])
        mapDataAttr.put(attributeName, attributeName)
    }

    void addColumnData(String attributeName, String type, int colWidth, boolean visible) {
        addColumnData(attributeName, attributeName, type, colWidth, visible)
    }

    JSONObject toJSON() {
        def jSONObject = new JSONObject();
        jSONObject.put("fields", getFields())
        jSONObject.put("columns", getColumns())
        jSONObject.put("datas", getDatas())
        jSONObject.put("url", url)
        jSONObject.put("name", name)
        return jSONObject
    }


}
