package com.domain.wechat

import com.domain.BaseDomain
import com.domain.api.ApiAccount
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'unionid')
@ToString(includes = 'openid', includeNames = false, includePackage = false)
class WxUser extends BaseDomain {
    Integer subscribe
    String openid
    byte[] nickname
    Integer sex
    String language
    byte[] city
    byte[] province
    String country
    String headimgurl
    Long subscribetime
    String unionid
    String remark
    Integer groupid
    String[] tagidlist
    ApiAccount apiAccount
    WxGroup wxGroup
    boolean isDelete

    String getStringNickname() {
        if (nickname)
            return new String(nickname,"utf-8")
        else return ""
    }

    String getStringProvince() {
        if (province)
            return new String(province,"utf-8")
        else return ""
    }

    String getStringCity() {
        if (city)
            return new String(city,"utf-8")
        else return ""
    }

    WxGroup getGroup() {
        return WxGroup.findByWxId(groupid)
    }

    static constraints = {
        subscribe nullable: true
        openid blank: false
        nickname nullable: true
        sex nullable: true
        language nullable: true
        city nullable: true
        province nullable: true
        country nullable: true
        headimgurl nullable: true
        subscribetime nullable: true
        unionid nullable: true
        remark nullable: true
        groupid nullable: true
        tagidlist nullable: true
        apiAccount nullable: false
        wxGroup nullable: true
        openid(unique: 'apiAccount')
        isDelete default: false

    }
    static mapping = {
        nickname sqlType: 'MEDIUMBLOB'
        province sqlType: 'MEDIUMBLOB'
        city sqlType: 'MEDIUMBLOB'
    }


}
