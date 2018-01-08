package scout.boot.kotlin.standard.ui.admin.role

import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.service.RoleService
import scout.boot.kotlin.standard.ui.admin.role.ReadAdministrationRolePagePermission
import scout.boot.kotlin.standard.ui.admin.role.RoleTablePage.Table
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm
import javax.inject.Inject

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable
import org.eclipse.scout.rt.client.ui.form.FormEvent
import org.eclipse.scout.rt.client.ui.form.FormListener
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter

@Bean
class RoleTablePage : AbstractPageWithTable<Table>() {

    @Inject
    private val roleService: RoleService? = null

    override fun getConfiguredTitle(): String {
        return TEXTS.get("RoleTablePage")
    }

    override fun execInitPage() {
        setVisiblePermission(ReadAdministrationRolePagePermission())
    }

    override fun getConfiguredLeaf(): Boolean {
        return true
    }

    override fun execLoadData(filter: SearchFilter?) {
        val roles = roleService!!.all
        importTableRowData(roles)
    }

    private fun importTableRowData(roles: Collection<Role>?) {
        val table = table

        table.deleteAllRows()

        if (roles == null || roles.size == 0) {
            return
        }

        for (role in roles) {
            val row = table.createRow()
            val roleId = role.id
            table.idColumn.setValue(row, roleId)
            table.nameColumn.setValue(row, TEXTS.getWithFallback(roleId, roleId))
            table.addRow(row)
        }
    }

    inner class Table : AbstractTable() {

        val nameColumn: NameColumn
            get() = columnSet.getColumnByClass(NameColumn::class.java)

        val idColumn: IdColumn
            get() = columnSet.getColumnByClass(IdColumn::class.java)

        override fun execRowAction(row: ITableRow?) {
            getMenuByClass(EditMenu::class.java).execAction()
        }

        @Order(10.0)
        inner class NewMenu : AbstractMenu() {

            override fun getConfiguredText(): String {
                return TEXTS.get("New")
            }

            override fun getConfiguredIconId(): String {
                return FontAwesomeIcons.fa_magic
            }

            override fun getConfiguredKeyStroke(): String {
                return "alt-n"
            }

            override fun getConfiguredMenuTypes(): Set<IMenuType> {
                return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection)
            }

            override fun execAction() {
                val form = BEANS.get(RoleForm::class.java)
                form.addFormListener(RoleFormListener())
                form.startNew()
            }
        }

        @Order(20.0)
        inner class EditMenu : AbstractMenu() {

            override fun getConfiguredText(): String {
                return TEXTS.get("Edit")
            }

            override fun getConfiguredIconId(): String {
                return FontAwesomeIcons.fa_pencil
            }

            override fun getConfiguredKeyStroke(): String {
                return "alt-e"
            }

            override fun getConfiguredMenuTypes(): Set<IMenuType> {
                return CollectionUtility.hashSet(TableMenuType.SingleSelection)
            }

            public override fun execAction() {
                val roleId = idColumn.selectedValue

                val form = BEANS.get(RoleForm::class.java)
                form.addFormListener(RoleFormListener())
                form.roleId = roleId
                form.startModify()
            }
        }

        @Order(30.0)
        inner class TranslateMenu : AbstractMenu() {
            override fun getConfiguredText(): String {
                return TEXTS.get("Translate")
            }

            override fun getConfiguredIconId(): String {
                return FontAwesomeIcons.fa_language
            }

            override fun getConfiguredKeyStroke(): String {
                return "alt-t"
            }

            override fun getConfiguredMenuTypes(): Set<IMenuType> {
                return CollectionUtility.hashSet(TableMenuType.SingleSelection)
            }

            override fun execAction() {
                val roleId = idColumn.selectedValue

                val form = BEANS.get(TranslationForm::class.java)
                form.key = roleId
                form.startModify()
                form.waitFor()

                if (form.isFormStored) {
                    reloadPage()
                }
            }
        }

        private inner class RoleFormListener : FormListener {

            override fun formChanged(e: FormEvent) {
                // reload page to reflect new/changed data after saving any changes
                if (FormEvent.TYPE_CLOSED == e.type && e.form.isFormStored) {
                    reloadPage()
                }
            }
        }

        @Order(10.0)
        inner class IdColumn : AbstractStringColumn() {

            override fun getConfiguredPrimaryKey(): Boolean {
                return true
            }

            override fun getConfiguredHeaderText(): String {
                return TEXTS.get("RoleName")
            }

            override fun getConfiguredWidth(): Int {
                return 150
            }
        }

        @Order(20.0)
        inner class NameColumn : AbstractStringColumn() {
            override fun getConfiguredHeaderText(): String {
                return TEXTS.get("RoleText")
            }

            override fun getConfiguredWidth(): Int {
                return 200
            }
        }

    }
}
