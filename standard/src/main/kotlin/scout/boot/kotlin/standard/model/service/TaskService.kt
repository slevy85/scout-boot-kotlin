package scout.boot.kotlin.standard.model.service

import scout.boot.kotlin.standard.model.Task
import java.util.UUID

interface TaskService : ValidatorService<Task> {

    fun getAll(): List<Task>

    fun getToday(userId: String): List<Task>

    fun getOwn(userId: String): List<Task>

    fun getInbox(userId: String): List<Task>

    fun exists(taskId: UUID): Boolean

    operator fun get(taskId: UUID): Task?

    fun save(task: Task)

}
