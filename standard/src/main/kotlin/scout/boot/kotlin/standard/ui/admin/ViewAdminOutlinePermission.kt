package scout.boot.kotlin.standard.ui.admin

import java.security.BasicPermission

class ViewAdminOutlinePermission : BasicPermission(ViewAdminOutlinePermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
