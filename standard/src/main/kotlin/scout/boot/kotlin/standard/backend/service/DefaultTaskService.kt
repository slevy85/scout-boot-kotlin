package scout.boot.kotlin.standard.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scout.boot.kotlin.standard.backend.entity.TaskEntity
import scout.boot.kotlin.standard.backend.repository.TaskRepository
import scout.boot.kotlin.standard.extensions.isToday
import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService
import java.util.*

@Service
class DefaultTaskService(private val taskRepository: TaskRepository) : TaskService, MapperService<Task, TaskEntity> {

    @Transactional(readOnly = true) override fun getAll() = taskRepository.findAll().map { convertToModel(it, Task::class.java) }

    @Transactional(readOnly = true) override fun getInbox(userId: String) = getUserTasks(userId).filter { !it.accepted }

    @Transactional(readOnly = true) override fun getOwn(userId: String) = getUserTasks(userId).filter { it.accepted }

    @Transactional(readOnly = true) override fun getToday(userId: String) = getUserTasks(userId).filter {
        it.accepted && !it.done && (it.dueDate.isToday() || it.reminder.isToday())
    }

    @Transactional(readOnly = true) override fun exists(taskId: UUID) = taskRepository.exists(taskId)

    @Transactional(readOnly = true) override fun get(taskId: UUID): Task? {
        val task = taskRepository.findOne(taskId) ?: return null
        return convertToModel(task, Task::class.java)
    }

    @Transactional override fun save(task: Task) {
        taskRepository.save(convertToEntity(task, TaskEntity::class.java))
    }

    protected fun getUserTasks(userId: String) = taskRepository.findByResponsible(userId).map { task -> convertToModel(task, Task::class.java) }
}
