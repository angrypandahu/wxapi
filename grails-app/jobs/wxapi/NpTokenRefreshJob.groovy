package wxapi

import com.utils.DateUtils

class NpTokenRefreshJob {
    def wxService
    static triggers = {
        cron name: 'NpTokenRefreshTrigger', cronExpression: "0 1 0/3 * * ?"
    }

    def execute() {
    /*    println("NpTokenRefreshJob->Start#####" + DateUtils.dateFormat_4.format(new Date()))
        wxService.npToken()
        println("NpTokenRefreshJob->End#####" + DateUtils.dateFormat_4.format(new Date()))*/
    }
}
