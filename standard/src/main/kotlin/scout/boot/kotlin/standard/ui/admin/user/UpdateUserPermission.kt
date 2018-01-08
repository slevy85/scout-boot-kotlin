package scout.boot.kotlin.standard.ui.admin.user

import java.security.BasicPermission

class UpdateUserPermission : BasicPermission(UpdateUserPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
