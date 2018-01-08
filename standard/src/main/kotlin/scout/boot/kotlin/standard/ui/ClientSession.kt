package scout.boot.kotlin.standard.ui

import scout.boot.kotlin.standard.model.service.UserService

import javax.inject.Inject

import org.eclipse.scout.boot.ui.scout.AbstractScoutBootClientSession
import org.eclipse.scout.rt.client.IClientSession
import org.eclipse.scout.rt.client.session.ClientSessionProvider
import org.eclipse.scout.rt.platform.BEANS

/**
 * Session class for the Eclipse Scout user interface.
 */
class ClientSession : AbstractScoutBootClientSession() {

    private var userId = ""

    @Inject
    lateinit private var userService: UserService

    override fun execLoadSession() {
        initCurrentUser()
        desktop = BEANS.get(Desktop::class.java) // lookup via BeanManager to support auto-wiring.
    }

    private fun initCurrentUser() {
        if (subject != null && !subject.principals.isEmpty()) {
            userId = subject.principals.iterator().next().name

            val user = userService.get(userId)
            if (user?.locale != null) {
                locale = user.locale
            }
        }
    }

    override fun getUserId(): String {
        return userId
    }

    companion object {

        /**
         * @return The [IClientSession] which is associated with the current thread, or `null` if not found.
         */
        fun get(): ClientSession? {
            return ClientSessionProvider.currentSession(ClientSession::class.java)
        }
    }
}
