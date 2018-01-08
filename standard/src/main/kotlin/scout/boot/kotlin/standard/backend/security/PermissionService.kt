package scout.boot.kotlin.standard.backend.security

import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.inventory.ClassInventory
import org.eclipse.scout.rt.platform.inventory.IClassInfo
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.platform.util.StringUtility
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService
import org.slf4j.LoggerFactory
import scout.boot.kotlin.standard.ui.TextDbProviderService
import java.security.BasicPermission
import java.security.Permission
import java.util.*
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Class copied from scout.rt.server package.
 */
@Order(4900.0)
class PermissionService : IPermissionService {

    private val m_permissionClassesLock = Any()
    private var m_permissionClasses: Set<Class<out Permission>>? = null
    private val m_permissionMap = HashMap<String, Permission>()

    @Inject
    lateinit internal var textDbService: TextDbProviderService

    val permissionKeys: Set<String>
        get() {
            checkCache()
            return m_permissionMap.keys
        }

    /**
     * @return Permission classes from class inventory. By default all direct subclasses of [Permission] and
     * [BasicPermission] that are available in the [ClassInventory] are returned.
     */
    private //get BasicPermssion subclasses are not found directly, because jdk is not scanned by jandex.
    val permissionsFromInventory: Set<IClassInfo>
        get() {
            val inv = ClassInventory.get()
            val classes = inv.getAllKnownSubClasses(Permission::class.java)
            classes.addAll(inv.getAllKnownSubClasses(BasicPermission::class.java))
            return classes
        }

    /**
     * Returns the set of irrelevant (for this application) permissions.
     */
    private val permissionsToExclude: Set<String>
        get() = EXCLUDE_ARRAY.filterTo(HashSet()) { StringUtility.hasText(it) }

    /**
     * Populates permission cache before it is accessed by the application. Also fills in default translations if these
     * should be missing.
     */
    @PostConstruct
    fun populatePermissionCache() {
        checkCache()
        allPermissionClasses.forEach { checkTranslations(it.name) }
    }

    private fun checkTranslations(id: String) {
        val prefix = id.substring(0, id.lastIndexOf("."))
        val group = prefix.substring(prefix.lastIndexOf(".") + 1)
        val key = id.substring(id.lastIndexOf(".") + 1)

        checkTranslation(prefix, group)
        checkTranslation(id, key)
    }

    private fun checkTranslation(key: String, text: String) {
        if (StringUtility.hasText(key)) {
            val translations = textDbService.getTexts(key)
            if (translations == null || translations.size == 0) {
                val t1 = StringUtility.splitCamelCase(text)
                var t2 = t1.substring(0, 1).toUpperCase() + t1.substring(1)

                if (t2.endsWith(" Permission")) {
                    t2 = t2.substring(0, t2.indexOf(" Permission"))
                }

                textDbService.addText(key, Locale.ROOT, t2)
            }
        }
    }

    override fun getAllPermissionClasses(): Set<Class<out Permission>> {
        checkCache()
        return CollectionUtility.hashSet(m_permissionClasses)
    }

    /**
     * Gets permission from cache via it's class name (i.e. permissionclass.getName()).
     *
     * @param key
     * the fully classified class name of the permission
     * @return the permission class
     */
    fun getPermission(key: String) = m_permissionMap[key]

    private fun checkCache() {
        synchronized(m_permissionClassesLock) {
            // null-check with lock (valid check)
            if (m_permissionClasses == null) {
                val allKnownPermissions = permissionsFromInventory
                val excludePermissions = permissionsToExclude
                val discoveredPermissions: Set<Class<out Permission>>

                discoveredPermissions = processPermission(allKnownPermissions, excludePermissions)
                m_permissionClasses = CollectionUtility.hashSet(discoveredPermissions)
            }
        }
    }

    private fun processPermission(allPermissions: Set<IClassInfo>, excludePermissions: Set<String>): Set<Class<out Permission>> {
        val discoveredPermissions = HashSet<Class<out Permission>>(allPermissions.size)

        for (permInfo in allPermissions) {
            if (acceptClass(permInfo)) {
                try {
                    val permClass = permInfo.resolveClass() as Class<out Permission>

                    val name = permInfo.name()
                    if (!excludePermissions.contains(name)) {
                        discoveredPermissions.add(permClass)

                        val permission = Class.forName(permClass.name).newInstance() as Permission
                        m_permissionMap.put(name, permission)
                    }
                } catch (e: Exception) {
                    LOG.warn("Unable to load permission: " + e.localizedMessage)
                }

            }
        }

        return discoveredPermissions
    }

    /**
     * Checks whether the given class is a Permission class that should be visible to this service. The default
     * implementation checks if the class meets the following conditions:
     *
     *  * class is instanciable (public, not abstract, not interface, not inner member type)
     *  * the name is accepted by [.acceptClassName]
     *
     *
     * @param permInfo
     * the class to be checked
     * @return Returns `true` if the class used by this service. `false` otherwise.
     */
    protected fun acceptClass(permInfo: IClassInfo): Boolean {
        return permInfo.isInstanciable && acceptClassName(permInfo.name())
    }

    /**
     * Checks whether the given class name is a potential permission class and used by this service.
     *
     * @param className
     * the class name to be checked
     * @return Returns `true` by default.
     */
    protected fun acceptClassName(className: String): Boolean {
        return true
    }

    companion object {

        // list of Scout permissions that are irrelevant for this application
        val EXCLUDE_ARRAY = arrayOf("org.eclipse.scout.rt.shared.security.CreateGlobalBookmarkPermission", "org.eclipse.scout.rt.shared.security.ReadGlobalBookmarkPermission", "org.eclipse.scout.rt.shared.security.UpdateGlobalBookmarkPermission", "org.eclipse.scout.rt.shared.security.DeleteGlobalBookmarkPermission", "org.eclipse.scout.rt.shared.security.CreateUserBookmarkPermission", "org.eclipse.scout.rt.shared.security.ReadUserBookmarkPermission", "org.eclipse.scout.rt.shared.security.UpdateUserBookmarkPermission", "org.eclipse.scout.rt.shared.security.DeleteUserBookmarkPermission", "org.eclipse.scout.rt.shared.security.PublishUserBookmarkPermission", "org.eclipse.scout.rt.shared.security.CreateCustomColumnPermission", "org.eclipse.scout.rt.shared.security.UpdateCustomColumnPermission", "org.eclipse.scout.rt.shared.security.DeleteCustomColumnPermission", "org.eclipse.scout.rt.shared.security.ReadDiagnosticServletPermission", "org.eclipse.scout.rt.shared.security.UpdateDiagnosticServletPermission", "org.eclipse.scout.rt.shared.security.RemoteServiceAccessPermission", "org.eclipse.scout.rt.shared.security.UpdateServiceConfigurationPermission")

        private val LOG = LoggerFactory.getLogger(PermissionService::class.java)
    }
}
