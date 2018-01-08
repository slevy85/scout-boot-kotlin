package scout.boot.kotlin.standard.ui.admin.role

import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.service.RoleService
import scout.boot.kotlin.standard.ui.admin.role.RoleForm.MainBox.CancelButton
import scout.boot.kotlin.standard.ui.admin.role.RoleForm.MainBox.OkButton
import scout.boot.kotlin.standard.ui.admin.role.RoleForm.MainBox.RoleBox
import scout.boot.kotlin.standard.ui.admin.role.RoleForm.MainBox.RoleBox.PermissionTableField
import scout.boot.kotlin.standard.ui.admin.role.RoleForm.MainBox.RoleBox.RoleIdField
import scout.boot.kotlin.standard.ui.admin.user.CreateUserPermission
import scout.boot.kotlin.standard.ui.admin.user.UpdateUserPermission

import java.security.Permission

import javax.inject.Inject

import org.eclipse.scout.boot.ui.commons.AbstractDirtyFormHandler
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.exception.VetoException
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService

@Bean
class RoleForm : AbstractForm() {

    @Inject
    private val roleService: RoleService? = null

    var roleId: String
        get() = roleIdField.value
        set(roleId) {
            roleIdField.value = roleId
        }

    val cancelButton: CancelButton
        get() = getFieldByClass(CancelButton::class.java)

    val roleIdField: RoleIdField
        get() = getFieldByClass(RoleIdField::class.java)

    val permissionTableField: PermissionTableField
        get() = getFieldByClass(PermissionTableField::class.java)

    val roleBox: RoleBox
        get() = getFieldByClass(RoleBox::class.java)

    val okButton: OkButton
        get() = getFieldByClass(OkButton::class.java)

    protected val permissions: Collection<Class<out Permission>>
        get() = BEANS.get(IPermissionService::class.java).allPermissionClasses

    override fun computeExclusiveKey(): Any {
        return roleId
    }

    protected fun calculateSubTitle(): String {
        return roleId
    }

    override fun getConfiguredDisplayHint(): Int {
        return IForm.DISPLAY_HINT_VIEW
    }

    override fun getConfiguredTitle(): String {
        return TEXTS.get("Role")
    }

    fun startModify() {
        startInternalExclusive(ModifyHandler())
    }

    fun startNew() {
        startInternal(NewHandler())
    }

    @Order(1000.0)
    inner class MainBox : AbstractGroupBox() {

        @Order(1000.0)
        inner class RoleBox : AbstractGroupBox() {

            @Order(1000.0)
            inner class RoleIdField : AbstractStringField() {

                override fun getConfiguredLabel(): String {
                    return TEXTS.get("RoleName")
                }

                override fun getConfiguredMandatory(): Boolean {
                    return true
                }

                override fun getConfiguredMaxLength(): Int {
                    return 128
                }
            }

            @Order(2000.0)
            inner class PermissionTableField : AbstractTableField<PermissionTableField.Table>() {

                override fun getConfiguredLabelVisible(): Boolean {
                    return false
                }

                override fun getConfiguredGridW(): Int {
                    return 2
                }

                override fun getConfiguredGridH(): Int {
                    return 4
                }

                inner class Table : AbstractPermissionTable() {

                    override fun execReloadPage() {
                        reloadTableData()
                    }
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
            roleIdField.isEnabled = false

            val role = roleService!!.get(roleId)
            importFormFieldData(role)

            if (role == Role.ROOT) {
                permissionTableField.isEnabled = false
            }

            subTitle = calculateSubTitle()
        }

        override fun execStore() {
            val role = roleService!!.get(roleIdField.value)
            exportFormFieldData(role)

            roleService.save(role)
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }

        override fun getConfiguredOpenExclusive(): Boolean {
            return true
        }
    }

    inner class NewHandler : AbstractDirtyFormHandler() {

        override fun execLoad() {
            setEnabledPermission(CreateUserPermission())
            importFormFieldData(null)
        }

        override fun execStore() {
            if (roleService!!.exists(roleId)) {
                throw VetoException(TEXTS.get("AccountAlreadyExists", roleId))
            }

            val role = Role()
            exportFormFieldData(role)

            roleService.save(role)
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }
    }

    private fun importFormFieldData(role: Role?) {
        if (role != null) {
            roleIdField.value = role.id
        }

        val table = permissionTableField.table
        table.loadData(null)
        table.rows
                .forEach {
                    if (role != null) {
                        val pId = table.idColumn.getValue(it)
                        val assign = role == Role.ROOT || role.permissions.contains(pId)
                        table.assignedColumn.setValue(it, assign)
                    }
                }
    }

    private fun exportFormFieldData(role: Role) {
        role.id = roleIdField.value

        val table = permissionTableField.table
        for (row in table.rows) {
            val permission = table.idColumn.getValue(row)
            val assigned = table.assignedColumn.getValue(row)

            if (assigned) {
                role.permissions.add(permission)
            } else {
                role.permissions.remove(permission)
            }
        }
    }
}
