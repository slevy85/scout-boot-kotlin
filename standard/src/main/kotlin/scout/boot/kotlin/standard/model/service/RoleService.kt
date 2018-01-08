package scout.boot.kotlin.standard.model.service

import scout.boot.kotlin.standard.model.Role

interface RoleService : ValidatorService<Role> {

    /**
     * Returns all available roles.
     */
    val all: List<Role>

    /**
     * Returns true if a role with the provided id exists. Returns false otherwise.
     */
    fun exists(roleId: String): Boolean

    /**
     * Returns the role specified by the provided role id. If no such role exists, null is returned.
     */
    operator fun get(roleId: String): Role

    /**
     * Persists the provided role.
     */
    fun save(role: Role)

}
