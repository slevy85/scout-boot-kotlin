package scout.boot.kotlin.standard

import scout.boot.kotlin.standard.backend.controller.ReadApiPermission
import scout.boot.kotlin.standard.ui.admin.db.ReadDatabaseAdministrationConsolePermission

import org.eclipse.scout.boot.ui.security.AbstractScoutBootWebSecurityConfigurerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Spring Security Configuration
 *
 *
 * For formatting please refer to [Spring
 * Security Java Config Preview: Readability](https://spring.io/blog/2013/07/11/spring-security-java-config-preview-readability/)
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Configuration
    @Order(2)
    class DatabaseAdministrationConsoleWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            /** @formatter:off
             */
            http
                    .antMatcher(ServletConfiguration.H2_CONTEXT_PATH + "/**")
                    .authorizeRequests()
                    .anyRequest().hasAuthority(ReadDatabaseAdministrationConsolePermission::class.java.name)
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER) // only reuse UI HTTP-session (don't create new HTTP-session with HTTP basic authentication)
                    .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin() // allow "h2-console" to be embedded in Scout UI
                    .and()
                    .csrf()
                    .disable() // "h2-console" doesn't implement CSRF
            /** @formatter:on
             */
        }
    }

    @Configuration
    @Order(1)
    class ApiWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            /** @formatter:off
             */
            http
                    .antMatcher(ServletConfiguration.API_CONTEXT_PATH + "/**")
                    .authorizeRequests()
                    .anyRequest().hasAuthority(ReadApiPermission::class.java.name)
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER) // only reuse UI HTTP-session (don't create new HTTP-session with HTTP basic authentication)
            /** @formatter:on
             */
        }
    }

    @Configuration
    class SecurityScoutBootWebSecurityConfigurerAdapter : AbstractScoutBootWebSecurityConfigurerAdapter()
}
