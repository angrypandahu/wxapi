package wxapi

import com.domain.api.ApiAccount
import com.util.WxUtils
import com.utils.DateUtils

class NpWxUseMoveGroupJob {
    def wxGroupService
    def wxService

    static triggers = {
        cron name: 'NpWxUseMoveGroupTrigger', cronExpression: "0 5 0/3 * * ?"
    }

    def execute() {
       /* try {
            def date = new Date()
            println("NpWxUseMoveGroupJob->Start#####" + DateUtils.dateFormat_4.format(date))
            wxService.npToken()
            def npApiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)
            def token = npApiAccount.apiToken.accessToken
            wxGroupService.moveToOldGroup(npApiAccount, token)
            wxGroupService.moveToNewGroup(npApiAccount, token)

            println("NpWxUseMoveGroupJob->End#####" + DateUtils.dateFormat_4.format(new Date()))
        } catch (Exception e) {
            log.error(e.getMessage())
        }*/

        // execute job
    }


}
