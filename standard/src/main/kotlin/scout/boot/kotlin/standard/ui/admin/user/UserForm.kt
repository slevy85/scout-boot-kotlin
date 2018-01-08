package scout.boot.kotlin.standard.ui.admin.user

import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.User
import scout.boot.kotlin.standard.model.service.DocumentService
import scout.boot.kotlin.standard.model.service.PasswordService
import scout.boot.kotlin.standard.model.service.RoleService
import scout.boot.kotlin.standard.model.service.UserService
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.AccountLockedField
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.CancelButton
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.OkButton
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.PasswordField
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.RoleTableField
import scout.boot.kotlin.standard.ui.admin.user.UserForm.MainBox.UserBox

import java.util.HashSet
import java.util.stream.Collectors

import javax.inject.Inject

import org.eclipse.scout.boot.ui.commons.AbstractDirtyFormHandler
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn
import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.exception.PlatformException
import org.eclipse.scout.rt.platform.exception.VetoException
import org.eclipse.scout.rt.shared.TEXTS

@Bean
class UserForm : AbstractForm() {

    @Inject
    lateinit private var userService: UserService

    @Inject
    lateinit private var roleService: RoleService

    @Inject
    lateinit private var documentService: DocumentService

    @Inject
    lateinit private var passwordService: PasswordService

    var userId: String
        get() = userBox.userIdField.value
        set(userId) {
            userBox.userIdField.value = userId
        }

    val cancelButton: CancelButton
        get() = getFieldByClass(CancelButton::class.java)

    val userBox: UserBox
        get() = getFieldByClass(UserBox::class.java)

    val passwordField: PasswordField
        get() = getFieldByClass(PasswordField::class.java)

    private val roleTableField: RoleTableField
        get() = getFieldByClass(RoleTableField::class.java)

    val accountLockedField: AccountLockedField
        get() = getFieldByClass(AccountLockedField::class.java)

    val okButton: OkButton
        get() = getFieldByClass(OkButton::class.java)

    override fun computeExclusiveKey() = userId

    protected fun calculateSubTitle() = userId

    override fun getConfiguredDisplayHint() = IForm.DISPLAY_HINT_VIEW

    override fun getConfiguredTitle() = TEXTS.get("User")

    fun startModify() {
        startInternalExclusive(ModifyHandler())
    }

    fun startNew() {
        startInternal(NewHandler())
    }

    @Order(10.0)
    inner class MainBox : AbstractGroupBox() {

        @Order(10.0)
        inner class UserBox : AbstractUserBox()

        @Order(20.0)
        inner class PasswordField : AbstractPasswordField() {

            override fun execChangedValue() {
                validateField(passwordService)
            }
        }

        @Order(25.0)
        inner class AccountLockedField : AbstractBooleanField() {
            override fun getConfiguredLabel() = TEXTS.get("AccountIsLocked")
        }

        @Order(30.0)
        inner class RoleTableField : AbstractTableField<RoleTableField.Table>() {

            override fun getConfiguredLabelVisible() = false

            override fun getConfiguredGridW() = 2

            override fun getConfiguredGridH() = 4

            inner class Table : AbstractTable() {

                val assignedColumn: AssignedColumn
                    get() = columnSet.getColumnByClass(AssignedColumn::class.java)

                val roleColumn: RoleColumn
                    get() = columnSet.getColumnByClass(RoleColumn::class.java)

                val idColumn: IdColumn
                    get() = columnSet.getColumnByClass(IdColumn::class.java)

                @Order(10.0)
                inner class IdColumn : AbstractStringColumn() {

                    override fun getConfiguredPrimaryKey() = true

                    override fun getConfiguredDisplayable() = false
                }

                @Order(20.0)
                inner class RoleColumn : AbstractStringColumn() {
                    override fun getConfiguredHeaderText() = TEXTS.get("Role")

                    override fun getConfiguredWidth() = 200
                }

                @Order(30.0)
                inner class AssignedColumn : AbstractBooleanColumn() {
                    override fun getConfiguredHeaderText() = TEXTS.get("Assigned")

                    override fun getConfiguredWidth() = 50

                    override fun getConfiguredEditable() = true
                }
            }
        }

        @Order(100000.0)
        inner class OkButton : AbstractOkButton()

        @Order(101000.0)
        inner class CancelButton : AbstractCancelButton()
    }

    inner class ModifyHandler : AbstractDirtyFormHandler() {

        override fun execLoad() {
            setEnabledPermission(UpdateUserPermission())

            val user = userService.get(userId) ?: throw PlatformException(TEXTS.get("UserNotFound"))
            val picture = userService.getPicture(userId)

            val box = userBox
            box.userIdField.isEnabled = false
            box.importFormFieldData(user)
            box.importUserPicture(picture)
            importUserRoles(user)

            accountLockedField.value = !user.enabled

            form.subTitle = calculateSubTitle()
        }

        override fun execStore() {
            val user = userService.get(userId) ?: throw PlatformException(TEXTS.get("UserNotFound"))
            store(user)
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }

        override fun getConfiguredOpenExclusive() = true
    }

    inner class NewHandler : AbstractDirtyFormHandler() {

        override fun execLoad() {
            setEnabledPermission(CreateUserPermission())

            passwordField.isMandatory = true
            importUserRoles(null)
        }

        override fun execStore() {
            val userId = userId
            if (userService.exists(userId)) {
                throw VetoException(TEXTS.get("AccountAlreadyExists", userId))
            }

            store(User())
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }
    }

    private fun store(user: User) {
        val newUser = user.id == null
        val userId = userId
        val box = userBox
        box.exportFormFieldData(user)

        // handle password
        val password = passwordField.value
        val hash: String

        if (newUser) {
            hash = passwordService.calculatePasswordHash(password)
            user.passwordHash = hash
        } else if (password != null) {
            hash = passwordService.calculatePasswordHash(password)
            user.passwordHash = hash
        }

        // handle user locking
        user.enabled = !accountLockedField.value

        // handle user roles
        exportUserRoles(user)

        userService.save(user)

        // handle user picture
        val picture = box.exportUserPicture()
        if (picture != null) {
            userService.setPicture(userId, picture)
        }
    }

    private fun importUserRoles(user: User?) {
        val table = roleTableField.table
        val roles = roleService.all.map { it.id }

        roles.stream().forEach { e ->
            val row = table.createRow()
            val role = TEXTS.getWithFallback(e, e)

            table.idColumn.setValue(row, e)
            table.roleColumn.setValue(row, role)

            if (user != null) {
                if (user.roles!!.contains(e)) {
                    table.assignedColumn.setValue(row, true)
                }
            }
            table.addRow(row)
        }
    }

    private fun exportUserRoles(user: User) {
        val table = roleTableField.table
        val roles = HashSet<String>()

        table.rows.stream().forEach { r ->
            if (table.assignedColumn.getValue(r)) {
                roles.add(table.idColumn.getValue(r))
            }
        }

        user.roles = roles
    }
}
