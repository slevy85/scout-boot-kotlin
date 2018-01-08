package scout.boot.kotlin.standard.backend.controller

import org.springframework.core.SpringVersion
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import scout.boot.kotlin.standard.ServletConfiguration
import scout.boot.kotlin.standard.model.service.TaskService
import java.security.Principal
import java.util.*

/**
 * REST controller to provide permissioned read access to tasks.
 * Important: Renaming permission classes/packages needs to be reflected here.
 */
@RestController
@RequestMapping(ServletConfiguration.API_CONTEXT_PATH + "/tasks")
class TaskController(private val taskService: TaskService) {

    companion object {
        private const val READ_TASKS = "scout.boot.kotlin.standard.ui.business.task.ReadTaskPermission"
        private const val READ_ALL_TASKS = "scout.boot.kotlin.standard.ui.business.task.ViewAllTasksPermission"
        const val AUTH_OWN = "hasAuthority('$READ_TASKS')"
        const val AUTH_ALL = "$AUTH_OWN and hasAuthority('$READ_ALL_TASKS')"
    }

    @RequestMapping(path = arrayOf("", "/", "/info"))
    fun info(): Map<String, String> {
        val info = HashMap<String, String>()
        info.put("api", "Task API")
        info.put("version", "Spring " + SpringVersion.getVersion())
        return info
    }

    @RequestMapping("/{id}")
    @PreAuthorize(AUTH_OWN)
    fun showTaskById(@PathVariable id: String) = taskService.get(UUID.fromString(id))

    @RequestMapping("/inbox")
    @PreAuthorize(AUTH_OWN)
    fun inbox(principal: Principal) = taskService.getInbox(principal.name)

    @RequestMapping("/today")
    @PreAuthorize(AUTH_OWN)
    fun today(principal: Principal) = taskService.getToday(principal.name)

    @RequestMapping("/own")
    @PreAuthorize(AUTH_OWN)
    fun own(principal: Principal) = taskService.getOwn(principal.name)

    @RequestMapping("/all")
    @PreAuthorize(AUTH_ALL)
    fun all() = taskService.getAll()

}
