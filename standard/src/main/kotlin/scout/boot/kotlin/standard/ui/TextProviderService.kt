package scout.boot.kotlin.standard.ui

import org.eclipse.scout.rt.shared.services.common.text.AbstractDynamicNlsTextProviderService

/**
 * Manages translated texts from the application's text property files.
 */
class TextProviderService : AbstractDynamicNlsTextProviderService() {

    override fun getDynamicNlsBaseName(): String {
        return "scout.boot.kotlin.standard.ui.nls.Texts"
    }
}
