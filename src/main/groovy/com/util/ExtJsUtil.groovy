package com.util

import com.utils.MyStringUtils
import org.grails.web.json.JSONArray

/**
 * Created by hupanpan on 2017/3/17.
 *
 */
class ExtJsUtil {
    static formatPaging(params) {
        def start = params.remove('start')
        def limit = params.remove('limit')
        def extjsSort = params.remove('sort')
        if (extjsSort) {
            def sortJson = new JSONArray(extjsSort)
            sortJson.each {
                def sort = MyStringUtils.getFiledFromDataIndex(it["property"].toString());
                def order = it["direction"].toString().toLowerCase()
                params.sort = sort
                params.order = order
            }
        }
        params.max = limit
        params.offset = start

    }
}
