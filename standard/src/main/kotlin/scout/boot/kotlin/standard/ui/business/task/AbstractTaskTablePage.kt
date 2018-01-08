package scout.boot.kotlin.standard.ui.business.task

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType
import org.eclipse.scout.rt.client.ui.basic.cell.Cell
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.*
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable
import org.eclipse.scout.rt.client.ui.form.FormEvent
import org.eclipse.scout.rt.client.ui.form.FormListener
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.html.HTML
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.platform.util.date.DateUtility
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall
import org.slf4j.LoggerFactory
import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService
import scout.boot.kotlin.standard.ui.ClientSession
import scout.boot.kotlin.standard.ui.admin.user.UserLookupCall
import scout.boot.kotlin.standard.ui.admin.user.UserPictureProviderService
import scout.boot.kotlin.standard.ui.business.task.AbstractTaskTablePage.Table
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@Bean open class AbstractTaskTablePage : AbstractPageWithTable<Table>() {

    @Inject lateinit private var taskService: TaskService

    @Inject lateinit private var userPictureService: UserPictureProviderService

    protected val userId: String
        get() = ClientSession.get()!!.userId

    private val formatter: SimpleDateFormat
        get() = SimpleDateFormat("EEEE", ClientSession.get()!!.locale)

    /**
     * callback method to specify a collection of tasks to be displayed by this table page.
     *
     * @return
     */
    protected open val tasks: Collection<Task>?
        get() = null

    override fun getConfiguredTitle() = TEXTS.get("TaskTablePage")

    override fun getConfiguredLeaf() = true

    override fun execPageActivated() {
        reloadPage()
    }

    override fun execLoadData(filter: SearchFilter?) {
        LOG.info("Loading data from persistence layer")
        val tasks = tasks
        importTableRowData(tasks)
    }

    private fun importTableRowData(tasks: Collection<Task>?) {
        if (tasks == null) {
            return
        }

        val table = table
        table.deleteAllRows()

        if (tasks.isEmpty()) {
            return
        }

        val formatter = formatter
        for (task in tasks) {
            val row = table.createRow()

            table.idColumn.setValue(row, task.id)
            table.dueInColumn.setValue(row, getDueInValue(task.dueDate, formatter))
            table.dueDateColumn.setValue(row, task.dueDate)
            table.titleColumn.setValue(row, task.name)
            table.assignedByColumn.setValue(row, task.assignedBy)
            table.assignedAtColumn.setValue(row, task.assignedAt)
            table.responsibleColumn.setValue(row, task.responsible)
            table.reminderColumn.setValue(row, task.reminder)
            table.acceptedColumn.setValue(row, task.accepted)
            table.doneColumn.setValue(row, task.done)

            table.addRow(row)
        }
    }

    private fun getDueInValue(date: Date?, formatter: SimpleDateFormat): String {
        val today = Date()
        var days = DateUtility.getDaysBetween(Date(), date)

        if (today.after(date!!)) {
            days = -days
        }

        if (days < 0) {
            return if (days == -1) {
                TEXTS.get("Yesterday")
            } else if (days >= -7) {
                TEXTS.get("Last", formatter.format(date))
            } else {
                TEXTS.get("DaysAgo", Integer.toString(-days))
            }
        }

        return if (days > 0) {
            if (days == 1) {
                TEXTS.get("Tomorrow")
            } else if (days < 7) {
                formatter.format(date)
            } else if (days < 14) {
                TEXTS.get("Next", formatter.format(date))
            } else {
                TEXTS.get("InDays", Integer.toString(days))
            }
        } else TEXTS.get("Today")

    }

    inner class Table : AbstractTable() {

        val dueDateColumn: DueDateColumn
            get() = columnSet.getColumnByClass(DueDateColumn::class.java)

        val doneColumn: DoneColumn
            get() = columnSet.getColumnByClass(DoneColumn::class.java)

        val acceptedColumn: AcceptedColumn
            get() = columnSet.getColumnByClass(AcceptedColumn::class.java)

        val assignedByColumn: AssignedByColumn
            get() = columnSet.getColumnByClass(AssignedByColumn::class.java)

        val assignedAtColumn: AssignedAtColumn
            get() = columnSet.getColumnByClass(AssignedAtColumn::class.java)

        val dueInColumn: DueInColumn
            get() = columnSet.getColumnByClass(DueInColumn::class.java)

        val creatorPictureColumn: AssignedByIconColumn
            get() = columnSet.getColumnByClass(AssignedByIconColumn::class.java)

        val reminderColumn: ReminderColumn
            get() = columnSet.getColumnByClass(ReminderColumn::class.java)

        val responsibleColumn: ResponsibleColumn
            get() = columnSet.getColumnByClass(ResponsibleColumn::class.java)

        val titleColumn: TitleColumn
            get() = columnSet.getColumnByClass(TitleColumn::class.java)

        val idColumn: IdColumn
            get() = columnSet.getColumnByClass(IdColumn::class.java)

        override fun execRowAction(row: ITableRow?) {
            getMenuByClass(EditMenu::class.java).execAction()
        }

        @Order(1000.0) inner class NewMenu : AbstractMenu() {

            override fun getConfiguredText() = TEXTS.get("New")

            override fun getConfiguredIconId() = FontAwesomeIcons.fa_magic

            override fun getConfiguredKeyStroke(): String = "alt-n"

            override fun getConfiguredMenuTypes(): Set<IMenuType> = CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection)

            override fun getConfiguredVisible(): Boolean = accessAllowed()

            override fun execAction() {
                if (!accessAllowed()) {
                    return
                }

                val form = BEANS.get(TaskForm::class.java)
                form.addFormListener(TaskFormListener())
                form.startNew()
            }

            private fun accessAllowed(): Boolean = BEANS.get(IAccessControlService::class.java).checkPermission(CreateTaskPermission())
        }

        @Order(2000.0) inner class EditMenu : AbstractMenu() {

            override fun getConfiguredText(): String = TEXTS.get("Edit")

            override fun getConfiguredIconId(): String = FontAwesomeIcons.fa_pencil

            override fun getConfiguredKeyStroke(): String = "alt-e"

            override fun getConfiguredMenuTypes(): Set<IMenuType> = CollectionUtility.hashSet(TableMenuType.SingleSelection)

            override fun getConfiguredVisible(): Boolean = accessAllowed()

            public override fun execAction() {
                if (!accessAllowed()) {
                    return
                }

                val taskId = idColumn.selectedValue

                val form = BEANS.get(TaskForm::class.java)
                form.addFormListener(TaskFormListener())
                form.taskId = taskId
                form.startModify()

                form.waitFor()
                if (form.isFormStored) {
                    reloadPage()
                }
            }

            private fun accessAllowed(): Boolean = BEANS.get(IAccessControlService::class.java).checkPermission(ReadTaskPermission())
        }

        @Order(3000.0) inner class AcceptMenu : AbstractMenu() {

            override fun getConfiguredText(): String = TEXTS.get("Accept")

            override fun getConfiguredIconId(): String = FontAwesomeIcons.fa_check

            override fun getConfiguredKeyStroke(): String = "alt-a"

            override fun getConfiguredMenuTypes(): Set<IMenuType> = CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection)

            override fun getConfiguredVisible(): Boolean = accessAllowed()

            override fun execAction() {
                if (!accessAllowed()) {
                    return
                }

                var listHasChanged = false

                for (taskId in idColumn.selectedValues) {
                    if (acceptTask(taskId)) {
                        listHasChanged = true
                    }
                }

                if (listHasChanged) {
                    reloadPage()
                }
            }

            private fun acceptTask(taskId: UUID): Boolean {
                val task = taskService.get(taskId)

                if (task != null && task.responsible == ClientSession.get()!!.userId) {
                    task.accepted = true
                    taskService.save(task)

                    return true
                }

                return false
            }

            private fun accessAllowed(): Boolean = BEANS.get(IAccessControlService::class.java).checkPermission(UpdateTaskPermission())
        }

        private inner class TaskFormListener : FormListener {

            override fun formChanged(e: FormEvent) {
                // reload page to reflect new/changed data after saving any changes
                if (FormEvent.TYPE_CLOSED == e.type && e.form.isFormStored) {
                    reloadPage()
                }
            }
        }

        @Order(0.0) inner class IdColumn : AbstractColumn<UUID>() {

            override fun getConfiguredPrimaryKey(): Boolean = true

            override fun getConfiguredDisplayable(): Boolean = false
        }

        @Order(1500.0) inner class AssignedByIconColumn : AbstractStringColumn() {

            override fun getConfiguredHtmlEnabled(): Boolean = true

            override fun execDecorateCell(cell: Cell?, row: ITableRow?) {
                val resourceName = assignedByColumn.getValue(row)
                if (resourceName != null) {
                    val value = userPictureService.getBinaryResource(resourceName)

                    if (value != null) {
                        addAttachment(value)
                        cell!!.text = HTML.imgByBinaryResource(value.filename).cssClass("usericon-html").toHtml()
                    }
                }
            }

            override fun getConfiguredWidth(): Int = 50
        }

        @Order(2000.0) inner class AssignedByColumn : AbstractSmartColumn<String>() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("AssignedBy")

            override fun getConfiguredWidth(): Int = 150

            override fun getConfiguredLookupCall(): Class<out ILookupCall<String>> = UserLookupCall::class.java
        }

        @Order(2500.0) inner class AssignedAtColumn : AbstractDateTimeColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("AssignedAt")

            override fun getConfiguredWidth(): Int = 100
        }

        @Order(3000.0) inner class TitleColumn : AbstractStringColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("Title")

            override fun getConfiguredSummary(): Boolean = true

            override fun getConfiguredWidth(): Int = 300
        }

        @Order(4000.0) inner class ResponsibleColumn : AbstractSmartColumn<String>() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("Responsible")

            override fun getConfiguredSummary(): Boolean = true

            override fun getConfiguredWidth(): Int = 150

            override fun getConfiguredLookupCall(): Class<out ILookupCall<String>> = UserLookupCall::class.java
        }

        @Order(5000.0) inner class DueInColumn : AbstractStringColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("DueIn")

            override fun getConfiguredWidth(): Int = 150
        }

        @Order(6000.0) inner class DueDateColumn : AbstractDateColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("DueDate")

            override fun getConfiguredSummary(): Boolean = true

            override fun getConfiguredWidth(): Int = 100
        }

        @Order(7000.0) inner class ReminderColumn : AbstractDateTimeColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("Reminder")

            override fun getConfiguredWidth(): Int = 150
        }

        @Order(8000.0) inner class AcceptedColumn : AbstractBooleanColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("Accepted")

            override fun getConfiguredWidth(): Int = 100
        }

        @Order(9000.0) inner class DoneColumn : AbstractBooleanColumn() {
            override fun getConfiguredHeaderText(): String = TEXTS.get("Done")

            override fun getConfiguredWidth(): Int = 100
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractTaskTablePage::class.java)
    }
}
