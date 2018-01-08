package scout.boot.kotlin.standard.ui.admin.role

import scout.boot.kotlin.standard.ui.admin.text.AbstractTranslateMenu

import java.security.Permission

import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService

abstract class AbstractPermissionTable : AbstractTable() {

    val idColumn: IdColumn
        get() = columnSet.getColumnByClass(IdColumn::class.java)

    val groupColumn: GroupColumn
        get() = columnSet.getColumnByClass(GroupColumn::class.java)

    val textColumn: TextColumn
        get() = columnSet.getColumnByClass(TextColumn::class.java)

    val assignedColumn: AssignedColumn
        get() = columnSet.getColumnByClass(AssignedColumn::class.java)

    protected val permissions: Collection<Class<out Permission>>
        get() = BEANS.get(IPermissionService::class.java).allPermissionClasses

    protected abstract fun execReloadPage()

    fun getGroupId(permissionId: String): String {
        return permissionId.substring(0, permissionId.lastIndexOf("."))
    }

    @Order(10.0)
    inner class TranslateMenu : AbstractTranslateMenu() {

        protected override val textKey: String
            get() = idColumn.selectedValue

        override fun reload() {
            execReloadPage()
        }
    }

    @Order(20.0)
    inner class TranslateGroupMenu : AbstractTranslateMenu() {

        protected override val textKey: String
            get() = getGroupId(idColumn.selectedValue)

        override fun getConfiguredText(): String {
            return TEXTS.get("TranslateGroup")
        }

        override fun getConfiguredKeyStroke(): String {
            return "alt-g"
        }

        override fun reload() {
            execReloadPage()
        }
    }

    @Order(1000.0)
    inner class IdColumn : AbstractStringColumn() {

        override fun getConfiguredPrimaryKey(): Boolean {
            return true
        }

        override fun getConfiguredDisplayable(): Boolean {
            return false
        }
    }

    @Order(2000.0)
    inner class GroupColumn : AbstractStringColumn() {
        override fun getConfiguredHeaderText(): String {
            return TEXTS.get("PermissionGroup")
        }

        override fun getConfiguredWidth(): Int {
            return 100
        }
    }

    @Order(3000.0)
    inner class TextColumn : AbstractStringColumn() {
        override fun getConfiguredHeaderText(): String {
            return TEXTS.get("Permission")
        }

        override fun getConfiguredWidth(): Int {
            return 100
        }
    }

    @Order(4000.0)
    inner class AssignedColumn : AbstractBooleanColumn() {
        override fun getConfiguredHeaderText(): String {
            return TEXTS.get("Assigned")
        }

        override fun getConfiguredWidth(): Int {
            return 100
        }

        override fun getConfiguredEditable(): Boolean {
            return true
        }
    }

    fun loadData(filter: SearchFilter?) {
        deleteAllRows()
        permissions
                .forEach {
                    val row = createRow()
                    val id = it.name
                    val groupId = getGroupId(id)
                    val group = TEXTS.getWithFallback(groupId, groupId)
                    val name = TEXTS.getWithFallback(id, id)

                    idColumn.setValue(row, id)
                    groupColumn.setValue(row, group)
                    textColumn.setValue(row, name)

                    addRow(row)
                }
    }
}
