package scout.boot.kotlin.standard.backend.security

import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.service.UserService
import java.util.*

@Service
class DefaultUserDetailsService(private val userService: UserService) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        try {
            val userInJpa = userService.get(username) ?: throw UsernameNotFoundException("User not found")
            return ScoutUserDetails(userInJpa, execLoadPermissions(username))
        } catch (e: Exception) {
            throw UsernameNotFoundException(e.message)
        }

    }

    protected fun execLoadPermissions(userId: String): Set<String> {
        val roles = userService.getRoles(userId)
        val permissions = HashSet<String>()

        for (role in roles) {
            permissions.add("ROLE_" + role.id!!.toUpperCase())

            if (Role.ROOT == role) {
                permissions.addAll(BEANS.get(IPermissionService::class.java).allPermissionClasses
                        .mapTo(HashSet()) { it.name })

            } else {
                for (permissionId in role.permissions) {
                    permissions.add(permissionId)
                }
            }
        }

        return permissions
    }

}
