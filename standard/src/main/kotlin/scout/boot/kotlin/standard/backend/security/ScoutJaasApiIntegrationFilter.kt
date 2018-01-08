package scout.boot.kotlin.standard.backend.security

import java.security.Principal

import javax.security.auth.Subject
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest

import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter

class ScoutJaasApiIntegrationFilter : JaasApiIntegrationFilter() {

    override fun obtainSubject(request: ServletRequest?): Subject? {
        var subject: Subject? = super.obtainSubject(request)

        if (subject == null) {
            val req = request as HttpServletRequest?
            val principal = req!!.userPrincipal
            if (principal != null) {
                subject = BEANS.get(ServletFilterHelper::class.java).createSubject(principal)
            }
        }

        return subject
    }
}
