package com.util

import com.gs.collections.impl.factory.Lists
import com.utils.SSHMysql2

/**
 * Created by hupanpan on 2017/4/11.
 *
 */
import groovy.sql.Sql
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

class MysqlUtil {
    public static String Join(String[] s, String delimiter) {
        return Join(Lists.CreateStringList(s), delimiter);
    }

    public static String Join(List<String> coll, String delimiter) {
        if (coll.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();

        for (String x : coll)
            sb.append(x + delimiter);

        sb.delete(sb.length() - delimiter.length(), sb.length());

        return sb.toString();
    }

    static getWxUser(List<String> notInOpenList) {
        new SSHMysql2().go()
        def db_url = "jdbc:mysql://134.119.18.75:3306/lbsx"
        def username = "lbsx"
        def password = "lb1234sx**"
        def driverClass = "com.mysql.jdbc.Driver"
        def sql = Sql.newInstance(db_url, username, password, driverClass)
        JSONArray array = new JSONArray()
        def joinStr = Join(notInOpenList, "','")

        def sqlStr = "select * from wechat_user where openid not in ('" + joinStr + "') "
        println(sqlStr)
        sql.eachRow(sqlStr) { row ->
            def object = new JSONObject()
            object.put("openid", row.openid)
            object.put("mobile", row.mobile)
            object.put("created_at", row.created_at)
            array.put(object)
        }
        return array
    }
}
