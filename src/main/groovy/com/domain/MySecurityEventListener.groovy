package com.domain

import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

/**
 * Created by hupanpan on 2017/3/20.
 *
 */
class MySecurityEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    def privilegeService

    void onApplicationEvent(AuthenticationSuccessEvent event) {
        def principal = event.getAuthentication().getPrincipal()
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        def request = webUtils.getCurrentRequest()
        def session = request.getSession()
        def zTreeJSON = privilegeService.findZTreeJSON(principal.id)
        print(zTreeJSON)
        session.setAttribute("zTree", zTreeJSON.toString())
    }


}
