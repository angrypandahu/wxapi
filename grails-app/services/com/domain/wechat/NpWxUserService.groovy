package com.domain.wechat

import com.domain.api.ApiAccount
import com.util.MysqlUtil
import grails.transaction.Transactional

@Transactional
class NpWxUserService {

    def get(id) {
        return NpWxUser.get(id)
    }

    def batchSave(List<NpWxUser> list) {
        NpWxUser.withNewTransaction {
            list?.each {
                it.save(flush: true)
            }
        }
    }

    def batchSync() {
        log.info("###batchSync->start")
        def list = NpWxUser.findAll();
        List<String> notInOpenList = new ArrayList<>()
        if (list) {
            notInOpenList = list.openid
        }
        def jSONArray = MysqlUtil.getWxUser(notInOpenList)
        List<NpWxUser> needToSave = new ArrayList<>()
        jSONArray?.each {
            def npWxUser = new NpWxUser()
            npWxUser.setOpenid(it.openid)
            npWxUser.setMobile(it.mobile)
            npWxUser.setCreatedAt(it.created_at)
            needToSave.add(npWxUser)
        }
        batchSave(needToSave)
        log.info("###batchSync->end")
    }
}
