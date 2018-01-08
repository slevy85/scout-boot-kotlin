package scout.boot.kotlin.standard

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.scout.boot.ui.security.endpoint.AbstractSecurityScoutBootServletConfiguration
import org.eclipse.scout.boot.ui.security.endpoint.AbstractSecurityScoutUIiServletMvcEndpoint
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * Application's servlet configuration
 */
@Configuration
@EnableWebMvc
@Controller
@RequestMapping("/")
class ServletConfiguration : AbstractSecurityScoutBootServletConfiguration() {

    @Configuration
    class StandaloneMvcEndpoint : AbstractSecurityScoutUIiServletMvcEndpoint() {

        @RequestMapping("/**")
        @Throws(Exception::class)
        override fun handle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
            return defaultScoutUIiServletMvcEndpointHandleImplementation(request, response)
        }
    }

    companion object {

        const val API_CONTEXT_PATH = "/api"
        const val H2_CONTEXT_PATH = "/h2-console"
    }
}
