package com.domain.wechat

import com.domain.BaseDomain

class NpWxUser extends BaseDomain {
    String openid
    String mobile
    Date createdAt
    static constraints = {
    }
}
