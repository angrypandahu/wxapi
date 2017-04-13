import com.domain.MySecurityEventListener
import com.utils.WxSyncUtils

// Place your Spring DSL code here
beans = {
    mySecurityEventListener(MySecurityEventListener) {
        privilegeService = ref('privilegeService')
    }
    wxSyncUtils(WxSyncUtils) {
        wxService = ref('wxService')
        wxUserService = ref('wxUserService')
    }

}
