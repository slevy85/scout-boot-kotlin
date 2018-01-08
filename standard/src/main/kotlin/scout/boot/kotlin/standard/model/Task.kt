package scout.boot.kotlin.standard.model

import java.util.*

/**
 * Task model (business entity).
 *
 *
 * Tasks are assigned to users, have a due date and an optional reminder date.
 */
open class Task : Model<UUID>() {

    init {
        id = UUID.randomUUID()
    }

    open var name: String = ""
    var description: String? = null

    open var responsible: String = ""

    open var assignedBy: String = ""

    open var dueDate: Date = Date()
    open var assignedAt: Date = Date()

    var reminder: Date? = null

    open var accepted: Boolean = false
    open var done: Boolean = false

}
