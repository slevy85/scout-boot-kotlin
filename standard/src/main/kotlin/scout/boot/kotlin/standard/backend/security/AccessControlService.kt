package scout.boot.kotlin.standard.backend.security

import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.service.UserService

import java.security.AllPermission
import java.security.Permission
import java.security.PermissionCollection
import java.security.Permissions
import java.util.concurrent.TimeUnit

import javax.inject.Inject

import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.shared.ISession
import org.eclipse.scout.rt.shared.cache.ICacheBuilder
import org.eclipse.scout.rt.shared.services.common.security.AbstractAccessControlService
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * <h3>[AccessControlService]</h3> [IAccessControlService] service
 * that uses [ISession.getUserId] as internal cache key required by
 * [AbstractAccessControlService] implementation.
 */
class AccessControlService : AbstractAccessControlService<String>() {

    @Inject
    lateinit private var userService: UserService

    override fun getCurrentUserCacheKey() : String? = userIdOfCurrentSubject

    override fun createCacheBuilder(): ICacheBuilder<String, PermissionCollection> {
        val cacheBuilder = BEANS.get(ICacheBuilder::class.java) as ICacheBuilder<String, PermissionCollection>
        return cacheBuilder
                .withCacheId(AbstractAccessControlService.ACCESS_CONTROL_SERVICE_CACHE_ID)
                .withValueResolver(createCacheValueResolver())
                .withShared(false).withClusterEnabled(false).withTransactional(false)
                .withTransactionalFastForward(false).withTimeToLive(1L, TimeUnit.HOURS, false)
    }

    override fun clearCache() {
        LOG.info("clearing cache")
        super.clearCache()
    }

    public override fun execLoadPermissions(userId: String): PermissionCollection {
        LOG.info("loading permissions for user '$userId'")

        val roles = userService!!.getRoles(userId)
        val permissions = Permissions()

        // check for root role
        if (roles.contains(Role.ROOT)) {
            permissions.add(AllPermission())
        } else {
            for (role in roles) {
                for (permissionId in role.permissions) {
                    val permission = getPermission(permissionId)

                    if (permission != null) {
                        permissions.add(permission)
                    }
                }
            }
        }// collect all permissions from all non-root roles

        return permissions
    }

    private fun getPermission(permissionKey: String): Permission? {
        return BEANS.get(PermissionService::class.java).getPermission(permissionKey)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AccessControlService::class.java)
    }
}
