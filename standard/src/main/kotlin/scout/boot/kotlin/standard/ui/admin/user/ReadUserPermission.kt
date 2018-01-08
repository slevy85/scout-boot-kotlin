package scout.boot.kotlin.standard.ui.admin.user

import java.security.BasicPermission

class ReadUserPermission : BasicPermission(ReadUserPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
