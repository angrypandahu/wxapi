package com.domain.wechat

import com.domain.BaseDomain
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'appid')
@ToString(includes = 'name', includeNames = true, includePackage = false)
class WeChatSubscription extends BaseDomain {
    String appid
    String secret
    String name
    String accessToken
    static constraints = {
        appid nullable: true
        secret nullable: true
        name blank: false
        accessToken blank: false
    }
}
