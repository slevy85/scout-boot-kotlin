package scout.boot.kotlin.standard.ui.business.task

import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService
import scout.boot.kotlin.standard.ui.business.task.AbstractTaskTablePage.Table.AcceptMenu
import javax.inject.Inject

import org.eclipse.scout.rt.shared.TEXTS

class TodaysTaskTablePage : AbstractTaskTablePage() {

    @Inject
    private val taskService: TaskService? = null

    protected override val tasks: Collection<Task>?
        get() = taskService!!.getToday(userId)

    init {
        table.responsibleColumn.isDisplayable = false
        table.acceptedColumn.isDisplayable = false
        table.doneColumn.isDisplayable = false
        table.getMenuByClass(AcceptMenu::class.java).isVisible = false
    }

    override fun getConfiguredTitle(): String {
        return TEXTS.get("TodaysTasks")
    }

    override fun execPageActivated() {
        reloadPage()
    }
}
