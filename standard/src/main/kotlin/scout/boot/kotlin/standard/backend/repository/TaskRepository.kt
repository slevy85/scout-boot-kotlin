package scout.boot.kotlin.standard.backend.repository

import java.util.UUID

import scout.boot.kotlin.standard.backend.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<TaskEntity, UUID> {

    fun findByResponsible(userId: String): List<TaskEntity>

}
