package scout.boot

import org.eclipse.scout.boot.ui.spring.minimal.AbstractMinimalScoutBootServletConfiguration
import org.eclipse.scout.boot.ui.spring.minimal.AbstractMinimalScoutUIiServletMvcEndpoint
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.EnableWebMvc

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

@Configuration
@EnableWebMvc
@Controller
@RequestMapping("/")
class ServletConfiguration : AbstractMinimalScoutBootServletConfiguration() {

    @RequestMapping(value = "/login", method = arrayOf(RequestMethod.GET))
    @Throws(IOException::class)
    override fun loginRedirect(httpServletResponse: HttpServletResponse) {
        redirect(httpServletResponse)
    }

    @RequestMapping(value = "/logout", method = arrayOf(RequestMethod.GET))
    @Throws(IOException::class)
    override fun logoutRedirect(httpServletResponse: HttpServletResponse) {
        redirect(httpServletResponse)
    }

    @Configuration
    class MvcEndpoint : AbstractMinimalScoutUIiServletMvcEndpoint() {

        @RequestMapping("/**")
        @Throws(Exception::class)
        override fun handle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
            return defaultScoutUIiServletMvcEndpointHandleImplementation(request, response)
        }
    }

    @Throws(IOException::class)
    protected fun redirect(httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/")
    }
}
