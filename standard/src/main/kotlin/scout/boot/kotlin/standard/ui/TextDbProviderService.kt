package scout.boot.kotlin.standard.ui

import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.util.StringUtility
import org.eclipse.scout.rt.shared.services.common.text.ITextProviderService
import scout.boot.kotlin.standard.model.Text
import scout.boot.kotlin.standard.model.service.TextService
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Manages translated texts from the database. The Ordering of this service is lower than [TextProviderService]
 * which provides the default translations. Therefore, the application first tries to obtain a text from the database
 * text service and only afterward switches to the default.
 */
@Order(1000.0)
class TextDbProviderService : ITextProviderService {

    private val translationCache: MutableMap<String, String?> = HashMap()
    private var cacheIsValid = false

    @Inject
    lateinit internal var textService: TextService

    override fun getText(locale: Locale?, key: String, vararg messageArguments: String): String? {
        var locale = locale
        checkCache()

        val session = ClientSession.get()
        if (locale == null && session != null) {
            locale = session.locale
        }

        // try to get exact translation
        var text = translationCache[Text.toId(locale, key)]
        if (StringUtility.hasText(text)) {
            return text
        }

        // try to find the right language only
        if (locale != null) {
            val part = locale.toLanguageTag().split("[-_]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            locale = Locale.forLanguageTag(part[0])
            text = translationCache[Text.toId(locale, key)]

            if (StringUtility.hasText(text)) {
                return text
            }
        }

        // return default translation
        return translationCache[Text.toId(null, key)]
    }

    override fun getTextMap(locale: Locale): Map<String, String>? {
        checkCache()

        return null
    }

    fun addText(key: String, locale: Locale, translation: String) {
        checkCache()

        val text = Text(key, locale, translation)

        translationCache.put(text.id!!, text.text)
        textService.save(text)
    }

    fun deleteText(key: String, locale: Locale) {
        val textId = Text.toId(locale, key)

        translationCache.remove(textId)
        textService.delete(textId)
    }

    fun getTexts(key: String): Map<Locale, String?> {
        checkCache()
        // Using kotlin sequence to avoid multiple loop over the map
        return translationCache.asSequence()
                .filter { key == Text.toKey(it.key) }
                .filter { StringUtility.hasText(it.value) }
                .associate { Pair(Text.toLocale(it.key) ?: LOCALE_DEFAULT, it.value) }
    }

    /**
     * Loads text from repository if it is not valid.
     */
    private fun checkCache() {
        if (cacheIsValid) {
            return
        }

        textService.all
                .forEach { translationCache.put(it.id!!, it.text) }

        cacheIsValid = true
    }

    companion object {

        val LOCALE_DEFAULT: Locale = Locale.ROOT
    }
}
