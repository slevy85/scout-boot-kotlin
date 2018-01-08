package scout.boot.kotlin.standard.backend.entity

import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.User
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskEntity(
        @Id override var id: UUID?,
        @Column(nullable = false) override var name: String,
        @Column(nullable = false, length = User.ID_LENGTH_MAX) override var responsible: String,
        @Column(nullable = false) override var dueDate: Date,
        @Column(nullable = false, length = User.ID_LENGTH_MAX) override var assignedBy: String,
        @Column(nullable = false) override var assignedAt: Date,
        @Column override var accepted: Boolean,
        @Column override var done: Boolean) : Task() {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = super.hashCode()

}
