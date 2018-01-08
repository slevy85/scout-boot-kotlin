package scout.boot.kotlin.standard.ui.business.task

import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService

import javax.inject.Inject
import org.eclipse.scout.rt.shared.TEXTS

class InboxTablePage : AbstractTaskTablePage() {

    @Inject
    private val taskService: TaskService? = null

    protected override val tasks: Collection<Task>?
        get() = taskService!!.getInbox(userId)

    init {
        table.responsibleColumn.isDisplayable = false
        table.acceptedColumn.isDisplayable = false
        table.doneColumn.isDisplayable = false
    }

    override fun getConfiguredTitle(): String {
        return TEXTS.get("InboxTablePage")
    }

    override fun execPageActivated() {
        reloadPage()
    }
}
