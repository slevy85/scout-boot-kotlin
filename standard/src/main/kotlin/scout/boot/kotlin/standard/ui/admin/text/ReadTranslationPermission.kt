package scout.boot.kotlin.standard.ui.admin.text

import java.security.BasicPermission

class ReadTranslationPermission : BasicPermission(ReadTranslationPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
