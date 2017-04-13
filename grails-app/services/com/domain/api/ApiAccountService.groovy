package com.domain.api

import com.util.ExtGrid
import com.utils.Constants
import grails.transaction.Transactional

@Transactional
class ApiAccountService {

    def serviceMethod() {

    }

    def getTable() {
        ExtGrid tableData = new ExtGrid(true, true, "/apiAccount/index?isJson=1", "con_account", "/apiAccount/create")
        tableData.addColumnData("apiAccount.id.label", Constants.DATATYPE_STRING, 20, false)
        tableData.addColumnData("apiAccount.name.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("apiAccount.type.label", Constants.DATATYPE_STRING, 100, true)
        tableData.addColumnData("apiAccount.description.label", Constants.DATATYPE_STRING, 100, true)
        return tableData
    }


}
