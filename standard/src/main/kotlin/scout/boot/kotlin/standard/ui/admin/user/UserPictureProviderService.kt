package scout.boot.kotlin.standard.ui.admin.user

import java.net.URL
import java.util.concurrent.ConcurrentHashMap

import org.eclipse.scout.rt.client.services.common.icon.AbstractIconProviderService
import org.eclipse.scout.rt.client.services.common.icon.IconSpec
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.resource.BinaryResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Order(1000.0)
class UserPictureProviderService : AbstractIconProviderService() {

    private val icons: MutableMap<String, BinaryResource?>

    init {
        icons = ConcurrentHashMap()
    }

    public override fun findIconSpec(name: String): IconSpec? {
        val resource = icons[name] ?: return null

        return IconSpec(name, resource.content)
    }

    fun getBinaryResource(name: String): BinaryResource? {
        return icons[name]
    }

    override fun findResource(relativePath: String): URL? {
        LOG.warn("!!! returns null (not implemented) !!!")
        return null
    }

    fun addUserPicture(name: String, picture: ByteArray?) {
        val usericon = BinaryResource(name, picture)
        icons.put(name, usericon)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(UserPictureProviderService::class.java)
    }
}
