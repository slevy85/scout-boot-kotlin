package scout.boot.kotlin.standard.model

import java.security.Permission
import java.util.HashSet

/**
 * Application user role
 */
open class Role : Model<String> {

    /**
     * Returns the set of permissions associated with this role.
     *
     * @return The set of permissions represented by their fully qualified [Permission] class names
     */
    /**
     * Sets the set of permissions for this role.
     *
     * @param permissions
     * The set of permissions represented by their fully qualified [Permission] class names
     */
    open var permissions: MutableSet<String> = HashSet()

    constructor()

    constructor(roleId: String) : super(roleId)

    companion object {
        const val ROOT_ID = "Root"
        val ROOT = Role(ROOT_ID)
    }
}
