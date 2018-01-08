package scout.boot.kotlin.standard.model

import java.util.Locale

import javax.validation.constraints.Size

/**
 * Application texts (localized)
 *
 */
open class Text : Model<String> {

    /**
     * Represents the translation for the text key and locale defined in the id of this object.
     */
    @Size(max = TEXT_LENGTH_MAX, message = TEXT_ERROR_LENGTH)
    open var text: String? = null

    @Size(min = ID_LENGTH_MIN, max = ID_LENGTH_MAX, message = ID_ERROR_LENGTH)
    override var id: String? = null

    /**
     * Gets the text key encoded in the id of this text object.
     */
    val key: String?
        get() = toKey(id)

    /**
     * Gets the locale encoded in the id of this text object.
     */
    val locale: Locale?
        get() = toLocale(id)

    constructor()

    constructor(key: String, locale: Locale, translation: String) {
        id = toId(locale, key)
        text = translation
    }

    companion object {

        const val ID_LENGTH_MIN = 1
        const val ID_LENGTH_MAX = 128
        const val ID_ERROR_LENGTH = "TextIdErrorLength"

        val LOCALE_UNDEFINED : Locale = Locale.ROOT

        const val TEXT_LENGTH_MAX = 128
        const val TEXT_ERROR_LENGTH = "TextErrorLength"

        private val ID_SEPARATOR = ":"

        fun toId(locale: Locale?, key: String?): String {
            var locale = locale
            var key = key
            if (key == null) {
                key = ""
            }

            if (locale == null) {
                locale = LOCALE_UNDEFINED
            }

            return String.format("%s%s%s", convert(locale), ID_SEPARATOR, key)
        }

        fun toKey(id: String?): String? {
            return if (!idIsValid(id)) {
                null
            } else id!!.substring(id.indexOf(ID_SEPARATOR) + 1)

        }

        fun toLocale(id: String?): Locale? {
            return if (!idIsValid(id)) {
                null
            } else convert(id!!.substring(0, id.indexOf(ID_SEPARATOR)))

        }

        private fun idIsValid(id: String?): Boolean {

            if (id == null) {
                return false
            }

            val separatorIndex = id.indexOf(ID_SEPARATOR)

            if (separatorIndex <= 0) {
                return false
            }

            return separatorIndex + 1 < id.length

        }

        private fun convert(locale: Locale) = locale.toLanguageTag()

        private fun convert(locale: String) = Locale.forLanguageTag(locale)
    }
}
