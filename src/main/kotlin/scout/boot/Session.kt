package scout.boot

import org.eclipse.scout.boot.ui.scout.AbstractScoutBootClientSession
import org.eclipse.scout.rt.client.session.ClientSessionProvider
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean

import java.util.UUID

@Bean
class Session : AbstractScoutBootClientSession() {

    override fun execLoadSession() {
        desktop = BEANS.get(Desktop::class.java)
    }

    override fun getUserId() = UUID.randomUUID().toString()

    companion object {
        fun get() = ClientSessionProvider.currentSession(Session::class.java)
    }
}
