package scout.boot.kotlin.standard.ui.business.task

import java.security.BasicPermission

class ReadTaskPermission : BasicPermission(ReadTaskPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
