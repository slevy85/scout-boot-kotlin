package scout.boot.kotlin.standard.ui.business.task

import java.security.BasicPermission

class CreateTaskPermission : BasicPermission(CreateTaskPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
