package wxapi

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
        def date = new Date()
        println("NpWxUserUpdateJob->Start#####" + DateUtils.dateFormat_4.format(date))
        def npApiAccount = wxService.npToken()
        npWxUserService.batchSync()
        wxSyncUtils.getAllWxUsers(npApiAccount)
        wxSyncUtils.getWxUserInfo(npApiAccount)
        wxGroupService.moveToOldGroup(npApiAccount)
        wxGroupService.moveToNewGroup(npApiAccount)
        println("NpWxUserUpdateJob->End#####" + DateUtils.dateFormat_4.format(new Date()))
        // execute job
    }


}
