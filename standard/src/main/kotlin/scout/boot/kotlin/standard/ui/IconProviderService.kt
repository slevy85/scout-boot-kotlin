package scout.boot.kotlin.standard.ui

import org.eclipse.scout.rt.client.services.common.icon.AbstractIconProviderService
import org.eclipse.scout.rt.platform.Order
import java.net.URL

/**
 * Provides application icons that are packaged with this application (resources folder).
 */
@Order(2000.0)
class IconProviderService : AbstractIconProviderService() {

    override fun findResource(relativePath: String): URL? = ResourceBase::class.java.getResource("img/" + relativePath)

}
