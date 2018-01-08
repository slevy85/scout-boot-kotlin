package scout.boot.kotlin.standard.ui.admin.text

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.shared.TEXTS

abstract class AbstractTranslateMenu : AbstractMenu() {

    protected abstract val textKey: String

    protected abstract fun reload()

    override fun getConfiguredText(): String {
        return TEXTS.get("Translate")
    }

    override fun getConfiguredIconId(): String {
        return FontAwesomeIcons.fa_language
    }

    override fun getConfiguredKeyStroke(): String {
        return "alt-t"
    }

    override fun getConfiguredMenuTypes(): Set<IMenuType> {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection)
    }

    override fun execAction() {
        val textId = textKey

        val form = BEANS.get(TranslationForm::class.java)
        form.key = textId
        form.startModify()
        form.waitFor()

        if (form.isFormStored) {
            reload()
        }
    }
}
