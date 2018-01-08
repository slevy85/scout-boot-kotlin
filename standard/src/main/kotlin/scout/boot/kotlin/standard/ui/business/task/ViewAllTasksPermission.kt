package scout.boot.kotlin.standard.ui.business.task

import java.security.BasicPermission

class ViewAllTasksPermission : BasicPermission(ViewAllTasksPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
