package scout.boot.kotlin.standard.ui.admin.user

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType
import org.eclipse.scout.rt.client.ui.basic.cell.Cell
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable
import org.eclipse.scout.rt.client.ui.form.FormEvent
import org.eclipse.scout.rt.client.ui.form.FormListener
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.html.HTML
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter
import scout.boot.kotlin.standard.model.User
import scout.boot.kotlin.standard.model.service.UserService
import scout.boot.kotlin.standard.ui.admin.user.UserTablePage.Table
import javax.inject.Inject

@Bean
class UserTablePage : AbstractPageWithTable<Table>() {

    @Inject
    private val userService: UserService? = null

    @Inject
    private val userPictureService: UserPictureProviderService? = null

    override fun getConfiguredTitle(): String {
        return TEXTS.get("UserTablePage")
    }

    override fun getConfiguredLeaf(): Boolean {
        return true
    }

    override fun execLoadData(filter: SearchFilter?) {
        val users = userService!!.all
        importTableRowData(users)
    }

    private fun importTableRowData(users: Collection<User>?) {
        val table = table

        table.deleteAllRows()

        if (users == null || users.size == 0) {
            return
        }

        for (user in users) {
            val row = table.createRow()
            table.userIdColumn.setValue(row, user.id)
            table.firstNameColumn.setValue(row, user.firstName)
            table.lastNameColumn.setValue(row, user.lastName)
            table.isRootColumn.setValue(row, user.isRoot)
            table.isLockedColumn.setValue(row, !user.enabled)
            table.addRow(row)
        }
    }

    inner class Table : AbstractTable() {

        val isLockedColumn: IsLockedColumn
            get() = columnSet.getColumnByClass(IsLockedColumn::class.java)

        val userIdColumn: UserIdColumn
            get() = columnSet.getColumnByClass(UserIdColumn::class.java)

        val firstNameColumn: FirstNameColumn
            get() = columnSet.getColumnByClass(FirstNameColumn::class.java)

        val isRootColumn: IsRootColumn
            get() = columnSet.getColumnByClass(IsRootColumn::class.java)

        val lastNameColumn: LastNameColumn
            get() = columnSet.getColumnByClass(LastNameColumn::class.java)

        override fun execRowAction(row: ITableRow?) {
            getMenuByClass(EditMenu::class.java).execAction()
        }

        @Order(0.0)
        inner class UserPictureColumn : AbstractStringColumn() {

            override fun getConfiguredHtmlEnabled(): Boolean {
                return true
            }

            override fun execDecorateCell(cell: Cell?, row: ITableRow?) {
                val resourceName = userIdColumn.getValue(row)
                if (resourceName != null) {
                    val value = userPictureService!!.getBinaryResource(resourceName)

                    if (value != null) {
                        addAttachment(value)
                        cell!!.text = HTML
                                .imgByBinaryResource(value.filename)
                                .cssClass("usericon-html")
                                .toHtml()
                    }
                }
            }

            override fun getConfiguredWidth() = 50
        }

        @Order(1000.0)
        inner class UserIdColumn : AbstractStringColumn() {

            override fun getConfiguredPrimaryKey() = true

            override fun getConfiguredHeaderText() = TEXTS.get("UserName")

            override fun getConfiguredWidth() = 100
        }

        @Order(2000.0)
        inner class FirstNameColumn : AbstractStringColumn() {

            override fun getConfiguredHeaderText() = TEXTS.get("FirstName")

            override fun getConfiguredWidth() = 100
        }

        @Order(3000.0)
        inner class LastNameColumn : AbstractStringColumn() {
            override fun getConfiguredHeaderText() = TEXTS.get("LastName")

            override fun getConfiguredWidth() = 100
        }

        @Order(4000.0)
        inner class IsRootColumn : AbstractBooleanColumn() {
            override fun getConfiguredHeaderText() = TEXTS.get("RootColumn")


            override fun getConfiguredWidth() = 150
        }

        @Order(5000.0)
        inner class IsLockedColumn : AbstractBooleanColumn() {
            override fun getConfiguredHeaderText() = TEXTS.get("IsLocked")


            override fun getConfiguredWidth() = 120

        }

        @Order(1000.0)
        inner class NewMenu : AbstractMenu() {

            override fun getConfiguredText() = TEXTS.get("New")

            override fun getConfiguredIconId() = "font:awesomeIcons \uf0d0"

            override fun getConfiguredMenuTypes() = CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection)

            override fun execAction() {
                val form = BEANS.get(UserForm::class.java)
                form.addFormListener(UserFormListener())
                form.startNew()
            }
        }

        @Order(2000.0)
        inner class EditMenu : AbstractMenu() {

            override fun getConfiguredText() = TEXTS.get("Edit")

            override fun getConfiguredIconId() = "font:awesomeIcons \uf040"

            override fun getConfiguredMenuTypes() = CollectionUtility.hashSet(TableMenuType.SingleSelection)

            public override fun execAction() {
                val form = BEANS.get(UserForm::class.java)
                form.addFormListener(UserFormListener())
                form.userId = table.userIdColumn.selectedValue
                form.startModify()
            }
        }

        private inner class UserFormListener : FormListener {

            override fun formChanged(e: FormEvent) {
                // reload page to reflect new/changed data after saving any changes
                if (FormEvent.TYPE_CLOSED == e.type && e.form.isFormStored) {
                    reloadPage()
                }
            }
        }
    }
}
