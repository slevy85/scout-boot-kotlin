package scout.boot.kotlin.standard.ui.admin.role

import java.security.BasicPermission

class ReadAdministrationPermissionPagePermission : BasicPermission(ReadAdministrationPermissionPagePermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }

}
