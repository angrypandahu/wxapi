package com.domain.wechat

import com.domain.BaseDomain
import com.domain.api.ApiAccount
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'wxId')
@ToString(includes = 'name', includeNames = false, includePackage = false)
class WxGroup extends BaseDomain {
    int wxId
    String name
    int count
    ApiAccount apiAccount
    static constraints = {
    }
}
