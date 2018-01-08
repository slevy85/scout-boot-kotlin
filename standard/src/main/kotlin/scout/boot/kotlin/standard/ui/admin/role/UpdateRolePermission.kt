package scout.boot.kotlin.standard.ui.admin.role

import java.security.BasicPermission

class UpdateRolePermission : BasicPermission(UpdateRolePermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
