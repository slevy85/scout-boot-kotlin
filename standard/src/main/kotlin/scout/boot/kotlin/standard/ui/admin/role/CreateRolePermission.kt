package scout.boot.kotlin.standard.ui.admin.role

import java.security.BasicPermission

class CreateRolePermission : BasicPermission(CreateRolePermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }

}
