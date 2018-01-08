package scout.boot.kotlin.standard.ui.business.task

import org.eclipse.scout.rt.shared.TEXTS
import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService
import javax.inject.Inject

class AllTasksTablePage : AbstractTaskTablePage() {

    @Inject
    lateinit private var taskService: TaskService

    protected override val tasks: Collection<Task>?
        get() = taskService.getAll()

    init {
        table.reminderColumn.isVisible = false
    }

    override fun getConfiguredTitle() = TEXTS.get("AllTasks")

    override fun execInitPage() {
        setVisiblePermission(ViewAllTasksPermission())
    }

    override fun execPageActivated() {
        // NOOP
    }

}
