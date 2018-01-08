package scout.boot.kotlin.standard.ui.admin.user

import scout.boot.kotlin.standard.model.service.PasswordService

import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField
import org.eclipse.scout.rt.platform.status.MultiStatus
import org.eclipse.scout.rt.platform.status.Status
import org.eclipse.scout.rt.shared.TEXTS

open class AbstractPasswordField : AbstractStringField() {

    override fun getConfiguredLabel(): String {
        return TEXTS.get("Password")
    }

    override fun getConfiguredInputMasked(): Boolean {
        return true
    }

    /**
     * Verifies password value and sets error indicators accordingly.
     *
     * @return true if the password complies with the implemented password policy. in all other cases false is returned.
     */
    fun validateField(passwordService: PasswordService): Boolean {
        clearErrorStatus()

        if (value == null) {
            setError(TEXTS.get("PasswordEmptyError"))
            return false
        }

        if (value.length < PasswordService.PASSWORD_LENGTH_MIN) {
            setError(TEXTS.get("PasswordTooShortError", PasswordService.PASSWORD_LENGTH_MIN.toString()))
            return false
        }

        if (value.length > PasswordService.PASSWORD_LENGTH_MAX) {
            setError(TEXTS.get("PasswordTooLongError", PasswordService.PASSWORD_LENGTH_MAX.toString()))
            return false
        }

        // make sure that we catch all relevant password constrains
        if (!passwordService.matchesPasswordPolicy(value)) {
            setError(TEXTS.get("PasswordPolicyError"))
            return false
        }

        return true
    }

    fun setError(message: String) {
        val error = MultiStatus()
        val status = Status(message)
        error.add(status)
        errorStatus = error
    }
}
