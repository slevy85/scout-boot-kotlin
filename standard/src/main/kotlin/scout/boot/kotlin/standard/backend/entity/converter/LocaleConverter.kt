package scout.boot.kotlin.standard.backend.entity.converter

import java.util.Locale

import javax.persistence.AttributeConverter

class LocaleConverter : AttributeConverter<Locale, String> {

    override fun convertToDatabaseColumn(locale: Locale?): String? {
        return locale?.toLanguageTag()

    }

    override fun convertToEntityAttribute(locale: String?): Locale? {
        return if (locale == null) {
            null
        } else Locale.forLanguageTag(locale)

    }

}
