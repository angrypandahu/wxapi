package wxapi

import com.domain.api.ApiAccount
import com.util.WxUtils
import com.utils.DateUtils

class NpWxUserSyncJob {
    def npWxUserService
    def wxSyncUtils
    def wxService
    def wxGroupService

    static triggers = {
        cron name: 'NpWxUserSyncTrigger', cronExpression: "0 0 0/3 * * ?"
    }

    def execute() {
        try {
            def date = new Date()
            println("NpWxUserSyncJob->Start#####" + DateUtils.dateFormat_4.format(date))
            def token = wxService.npToken()
            def npApiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)
            npWxUserService.batchSync()

            wxSyncUtils.getAllWxUsers(npApiAccount)

            wxSyncUtils.getWxUserInfoNoThread(npApiAccount)

            wxGroupService.moveToOldGroup(npApiAccount, token)

            wxGroupService.moveToNewGroup(npApiAccount, token)

            println("NpWxUserSyncJob->End#####" + DateUtils.dateFormat_4.format(new Date()))
        } catch (Exception e) {
            log.error(e.getMessage())
        }
    }
}
