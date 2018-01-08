package scout.boot.kotlin.standard.ui.admin.text

import java.security.BasicPermission

class CreateTranslationPermission : BasicPermission(CreateTranslationPermission::class.java.simpleName) {
    companion object {

        private val serialVersionUID = 1L
    }
}
