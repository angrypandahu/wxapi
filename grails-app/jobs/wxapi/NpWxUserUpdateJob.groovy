package wxapi

import com.domain.api.ApiAccount
import com.util.WxUtils
import com.utils.DateUtils

class NpWxUserUpdateJob {
    def wxService
    def wxSyncUtils
    def wxGroupService
    def npWxUserService
    static triggers = {
        cron name: 'NpWxUserUpdateTrigger', cronExpression: "0 0 0/3 * * ?"
    }

    def execute() {
        try {
            def date = new Date()
            println("NpWxUserUpdateJob->Start#####" + DateUtils.dateFormat_4.format(date))
            def npApiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)
            def token = wxService.npToken(true)
            npWxUserService.batchSync()
            wxSyncUtils.getAllWxUsers(npApiAccount)
            wxSyncUtils.getWxUserInfo(npApiAccount)

            wxGroupService.moveToOldGroup(npApiAccount, token)
            wxGroupService.moveToNewGroup(npApiAccount, token)

            println("NpWxUserUpdateJob->End#####" + DateUtils.dateFormat_4.format(new Date()))
        } catch (Exception e) {
            log.error(e.getMessage())
        }

        // execute job
    }


}
