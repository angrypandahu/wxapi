package com.domain.api

import com.domain.auth.User
import com.domain.wechat.NpWxUser
import com.domain.wechat.WxGroup
import com.domain.wechat.WxUser
import com.util.MysqlUtil
import grails.transaction.Transactional
import wxapi.NpWxUserUpdateJob

@Transactional(readOnly = true)
class WxController {

    def apiOrgService
    def wxService
    def wxUserService
    def wxSyncUtils
    def wxGroupService
    def npWxUserService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def userId = getAuthenticatedUser().id
        def apiAccountList = UserApiAccount.findAllByUser(User.load(userId))?.apiAccount
        render view: 'index', model: [zTree: apiOrgService.findZTreeJSON(userId), apiAccountList: apiAccountList]
    }

    def show() {
        def apiOrg = ApiOrg.load(params.id)
        def userId = getAuthenticatedUser().id
        def apiAccount = ApiAccount.load(params.account)
        respond apiOrg, model: [zTree: apiOrgService.findZTreeJSON(userId), apiAccount: apiAccount]
    }


    def token() {
        def apiAccount = ApiAccount.load(params.account)
        render(wxService.token(apiAccount).accessToken)
    }


    def getWxUsers() {
        def begin = new Date()
        def apiAccount = ApiAccount.load(params.account)
        def criteria = WxUser.where {
            apiAccount == apiAccount
        }
        criteria.updateAll(isDelete: true)
        wxSyncUtils.getWxUsers(apiAccount, "")
        def end = new Date()
        def i = (end.getTime() - begin.getTime()) / 1000
        render("耗时:" + i + "秒")
    }

    def getWxUserInfo() {
        def begin = new Date()
        def apiAccount = ApiAccount.load(params.account)
        wxSyncUtils.getWxUserInfo(apiAccount)
        def end = new Date()
        def i = (end.getTime() - begin.getTime()) / 1000
        render("耗时:" + i + "秒")
    }

    def getWxGroup() {
        def apiAccount = ApiAccount.load(params.account)
        render(wxGroupService.getWxGroup(apiAccount))
    }

    def wxGroupUserUpdateAll() {
        def apiAccount = ApiAccount.load(params.account)
        def openidList = NpWxUser.findAll().openid
        def wxUsers = WxUser.findAllByUnionidInList(openidList)
//        log.info(wxUsers.size())
        def wxGroup = WxGroup.findByNameAndApiAccount("老用户", apiAccount)
        wxUsers.each {
            wxGroupService.wxGroupUserUpdate(it, wxGroup)
        }
        render("")
    }

    def wxGroupUserUpdate() {
        def wxUser = WxUser.load(params.wxUserId)
        def wxGroup = WxGroup.load(params.wxGroupId)
        render(wxGroupService.wxGroupUserUpdate(wxUser, wxGroup).toString())
    }

    def getNpUser() {
        def findAll = npWxUserService.get(8)
        render(findAll.toString())
    }

    def syncWxUser() {
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
        npWxUserService.batchSave(needToSave)
        render(needToSave.size())
    }

    def createGroup() {
        def apiAccount = ApiAccount.load(params.account)
        def group = new WxGroup()
        group.setWxId(Integer.parseInt(params.groupid))
        group.setName(params.name)
        group.setApiAccount(apiAccount)
        def group1 = wxGroupService.createWxGroup(group)
        render(group1.toString())
    }

    def job() {
        NpWxUserUpdateJob.triggerNow()
        render("job")
    }

    def toNewGroup() {
        def apiAccount = ApiAccount.load(params.account)
        wxGroupService.moveToNewGroup(apiAccount)
    }


}
