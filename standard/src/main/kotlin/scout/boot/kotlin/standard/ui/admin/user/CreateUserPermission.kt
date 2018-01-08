package scout.boot.kotlin.standard.ui.admin.user

import java.security.BasicPermission

class CreateUserPermission : BasicPermission(CreateUserPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
