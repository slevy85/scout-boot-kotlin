package scout.boot.kotlin.standard.backend.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import scout.boot.kotlin.standard.model.User

internal class ScoutUserDetails(private val user: User, set: Set<String>) : UserDetails {
    private val authorities: Set<SimpleGrantedAuthority>

    init {
        this.authorities = set.mapTo(HashSet()) { p -> SimpleGrantedAuthority(p) }
    }

    override fun getAuthorities() = authorities

    override fun getPassword() = user.passwordHash

    override fun getUsername() = user.id

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = user.enabled

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = user.enabled

    companion object {
        private val serialVersionUID = -62907629237667118L
    }

}
