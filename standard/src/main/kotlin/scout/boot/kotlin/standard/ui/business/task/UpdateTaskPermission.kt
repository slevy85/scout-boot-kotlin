package scout.boot.kotlin.standard.ui.business.task

import java.security.BasicPermission

class UpdateTaskPermission : BasicPermission(UpdateTaskPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
