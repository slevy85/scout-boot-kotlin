package scout.boot.kotlin.standard.backend.controller

import java.security.BasicPermission

class ReadApiPermission : BasicPermission(ReadApiPermission::class.java.simpleName) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
