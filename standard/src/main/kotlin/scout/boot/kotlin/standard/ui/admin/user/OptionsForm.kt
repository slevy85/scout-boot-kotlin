package scout.boot.kotlin.standard.ui.admin.user

import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.User
import scout.boot.kotlin.standard.model.service.DocumentService
import scout.boot.kotlin.standard.model.service.PasswordService
import scout.boot.kotlin.standard.model.service.UserService
import scout.boot.kotlin.standard.ui.ClientSession
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ApplyButton
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.UserBox
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.CancelPasswordChangeLink
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.ChangePasswordLink
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.ConfirmPasswordField
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.NewPasswordField
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.OldPasswordField
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm.MainBox.ChangePasswordBox.UpdateLinkButton

import javax.inject.Inject

import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler
import org.eclipse.scout.rt.client.ui.form.fields.AbstractFormField
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractLinkButton
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.placeholder.AbstractPlaceholderField
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.exception.PlatformException
import org.eclipse.scout.rt.shared.TEXTS

@Bean
class OptionsForm : AbstractForm() {

    @Inject
    lateinit private var userService: UserService

    @Inject
    lateinit private var documentService: DocumentService

    @Inject
    lateinit private var passwordService: PasswordService

    var userId: String
        get() = userBox.userIdField.value
        set(userId) {
            userBox.userIdField.value = userId
        }

    val userBox: UserBox
        get() = getFieldByClass(UserBox::class.java)

    val oldPasswordField: OldPasswordField
        get() = getFieldByClass(OldPasswordField::class.java)

    val cancelPasswordChangeField: CancelPasswordChangeLink
        get() = getFieldByClass(CancelPasswordChangeLink::class.java)

    val verifyLinkButton: UpdateLinkButton
        get() = getFieldByClass(UpdateLinkButton::class.java)

    val newPasswordField: NewPasswordField
        get() = getFieldByClass(NewPasswordField::class.java)

    val confirmPasswordField: ConfirmPasswordField
        get() = getFieldByClass(ConfirmPasswordField::class.java)

    val changePasswordLink: ChangePasswordLink
        get() = getFieldByClass(ChangePasswordLink::class.java)

    val changePasswordBox: ChangePasswordBox
        get() = getFieldByClass(ChangePasswordBox::class.java)

    val applyButton: ApplyButton
        get() = getFieldByClass(ApplyButton::class.java)

    init {
        userId = ClientSession.get()!!.userId
    }

    override fun start() {
        startInternal(ViewHandler())
    }

    override fun getConfiguredTitle(): String {
        return TEXTS.get("Options")
    }

    @Order(10.0)
    inner class MainBox : AbstractGroupBox() {

        @Order(10.0)
        inner class UserBox : AbstractUserBox()

        @Order(20.0)
        inner class ChangePasswordBox : AbstractSequenceBox() {

            internal var changeIt = false

            var value: Boolean
                get() = changeIt
                set(changeIt) {
                    this.changeIt = changeIt

                    applyButton.isEnabled = !changeIt
                    changePasswordLink.isVisible = !changeIt
                    oldPasswordField.isVisible = changeIt
                    newPasswordField.isVisible = changeIt
                    confirmPasswordField.isVisible = changeIt
                    verifyLinkButton.isVisible = changeIt
                    cancelPasswordChangeField.isVisible = changeIt

                    resetPasswordFieldValues()
                    clearPasswordErrorStates()
                }

            override fun getConfiguredLabel() = TEXTS.get("Password")

            override fun getConfiguredAutoCheckFromTo() = false

            override fun getConfiguredGridW() = 2

            override fun execInitField() {
                value = false
            }

            @Order(0.0)
            inner class ChangePasswordLink : AbstractLinkButton() {

                override fun getConfiguredLabel(): String {
                    return TEXTS.get("ChangePassword")
                }

                override fun execClickAction() {
                    applyButton.isEnabled = false
                    value = true
                }
            }

            @Order(10.0)
            inner class OldPasswordField : AbstractPasswordField() {

                override fun getConfiguredLabel() = TEXTS.get("OldPassword")

                override fun getConfiguredLabelPosition() = AbstractFormField.LABEL_POSITION_ON_FIELD
            }

            @Order(20.0)
            inner class NewPasswordField : AbstractPasswordField() {

                override fun getConfiguredLabel() = TEXTS.get("NewPassword")

                override fun getConfiguredLabelPosition() = AbstractFormField.LABEL_POSITION_ON_FIELD
            }

            @Order(30.0)
            inner class ConfirmPasswordField : AbstractPasswordField() {

                override fun getConfiguredLabel() = TEXTS.get("ConfirmPassword")

                override fun getConfiguredLabelPosition() = AbstractFormField.LABEL_POSITION_ON_FIELD
            }

            @Order(40.0)
            inner class UpdateLinkButton : AbstractLinkButton() {
                override fun getConfiguredLabel() = TEXTS.get("Update")

                override fun getConfiguredTooltipText() = TEXTS.get("ClickToEnableApplyButton")

                override fun execClickAction() {
                    if (validatePasswordChange()) {
                        applyButton.isEnabled = true
                        oldPasswordField.isEnabled = false
                        newPasswordField.isEnabled = false
                        confirmPasswordField.isEnabled = false
                    }
                }
            }

            @Order(50.0)
            inner class CancelPasswordChangeLink : AbstractLinkButton() {
                override fun getConfiguredLabel() = TEXTS.get("CancelPasswordChange")

                override fun execClickAction() {
                    value = false
                    applyButton.isEnabled = true
                    oldPasswordField.isEnabled = true
                    newPasswordField.isEnabled = true
                    confirmPasswordField.isEnabled = true
                }
            }
        }

        inner class DummyField : AbstractPlaceholderField() {
            // @Override
            // protected String getConfiguredCssClass() {
            // return "options-form-bottom-placeholder";
            // }

            override fun getConfiguredHeightInPixel() = 6

            override fun getConfiguredGridW() = 2
        }

        @Order(40.0)
        inner class ApplyButton : AbstractOkButton() {

            override fun getConfiguredLabel() = TEXTS.get("ApplyChanges")

            override fun execClickAction() {
                if (changePasswordBox.value) {
                    if (!validatePasswordChange()) {
                        return
                    }
                }

                super.execClickAction()
            }
        }

        private fun validatePasswordChange(): Boolean {
            clearPasswordErrorStates()

            if (!changePasswordBox.value) {
                return true
            }

            var ok = true
            ok = ok and validateOldPassword()
            ok = ok and validateNewPassword()
            ok = ok and validateConfirmPassword()

            return ok
        }

        private fun resetPasswordFieldValues() {
            oldPasswordField.value = null
            newPasswordField.value = null
            confirmPasswordField.value = null
        }

        private fun clearPasswordErrorStates() {
            oldPasswordField.clearErrorStatus()
            newPasswordField.clearErrorStatus()
            confirmPasswordField.clearErrorStatus()
        }

        private fun validateOldPassword(): Boolean {
            val user = userService.get(userId) ?: throw PlatformException(TEXTS.get("UserNotFound"))
            val password = oldPasswordField.value

            if (passwordService.passwordIsValid(password, user.passwordHash)) {
                oldPasswordField.setError(TEXTS.get("PasswordInvalid"))
                return false
            }

            return true
        }

        private fun validateNewPassword(): Boolean {
            return newPasswordField.validateField(passwordService)
        }

        private fun validateConfirmPassword(): Boolean {
            if (!confirmPasswordField.validateField(passwordService)) {
                return false
            }

            val passwordConfirm = confirmPasswordField.value
            val passwordNew = newPasswordField.value

            if (passwordConfirm != passwordNew) {
                confirmPasswordField.setError(TEXTS.get("PasswordMismatchError"))
                return false
            }

            return true
        }
    }

    inner class ViewHandler : AbstractFormHandler() {

        override fun execLoad() {
            reload()
        }

        override fun execStore() {
            val userId = userId
            val user = userService.get(userId) ?: throw PlatformException(TEXTS.get("UserNotFound"))
            val box = userBox

            // handle basic fields
            box.exportFormFieldData(user)

            // handle user picture
            val picture = box.exportUserPicture()
            if (picture != null) {
                user.pictureId = picture.id
                userService.setPicture(userId, picture)
            }

            // handle user password
            if (changePasswordBox.value) {
                val passwordNew = newPasswordField.value
                val passwordHash = passwordService.calculatePasswordHash(passwordNew)

                user.passwordHash = passwordHash
            }

            userService.save(user)
        }
    }

    /**
     * Reload needs to be public as it is triggered from outside while the form
     * is still open (but invisible).
     */
    fun reload() {
        val userId = userId
        val user = userService.get(userId)
        val picture = userService.getPicture(userId)

        val box = userBox
        box.importFormFieldData(user)
        box.importUserPicture(picture)
        box.userIdField.isEnabled = false
    }
}
