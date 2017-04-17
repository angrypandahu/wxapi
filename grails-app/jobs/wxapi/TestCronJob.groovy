package wxapi

import com.domain.api.ApiAccount
import com.domain.wechat.NpWxUser
import com.domain.wechat.WxUser
import com.util.WxUtils

class TestCronJob {
    static triggers = {
        cron name: 'TestCronTrigger', cronExpression: "0 2 0/9 * * ?"
    }
    def wxGroupService
    def wxService
    def npWxUserService
    def wxSyncUtils
    def wxUserService

    def execute() {
       /* try {
            println("TestCronJob######start")

            wxService.npToken(false)
//        println(" wxService.npToken(true)")
            def npApiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)
//            println("  def npApiAccount = ApiAccount.findByAppId(WxUtils.NP_WX_APP_ID)")
//        npWxUserService.batchSync()
//        println(" npWxUserService.batchSync()")

//        wxSyncUtils.getAllWxUsers(npApiAccount)
//        println("wxSyncUtils.getAllWxUsers(npApiAccount)")

            wxSyncUtils.getWxUserInfoNoThread(npApiAccount)



            println("TestCronJob######end")
        } catch (Exception e) {
            println("121")
        }*/

    }
}
