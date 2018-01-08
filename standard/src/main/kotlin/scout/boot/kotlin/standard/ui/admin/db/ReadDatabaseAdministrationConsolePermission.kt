package scout.boot.kotlin.standard.ui.admin.db

import java.security.BasicPermission

class ReadDatabaseAdministrationConsolePermission : BasicPermission(ReadDatabaseAdministrationConsolePermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
