package scout.boot.kotlin.standard.backend.service

import scout.boot.kotlin.standard.backend.entity.TextEntity
import scout.boot.kotlin.standard.backend.repository.TextRepository
import scout.boot.kotlin.standard.model.Text
import scout.boot.kotlin.standard.model.service.TextService
import java.util.Locale

import org.assertj.core.util.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultTextService : TextService, MapperService<Text, TextEntity> {

    @Autowired
    lateinit private var textRepository: TextRepository

    override val all: List<Text>
        @Transactional(readOnly = true)
        get() = textRepository.findAll()
                .map { convertToModel(it, Text::class.java) }

    @Transactional(readOnly = true)
    override fun getAll(key: String): List<Text> {
        return if (Strings.isNullOrEmpty(key)) {
            all
        } else all.filter { it.key == key }

    }

    @Transactional(readOnly = true)
    override fun getAll(locale: Locale?): List<Text> {

        if (locale == null || Text.LOCALE_UNDEFINED == locale) {
            return all
        }

        val localeId = locale.toLanguageTag()

        return all
                .filter { it.locale!!.toLanguageTag().startsWith(localeId) }

    }

    @Transactional(readOnly = true)
    override fun exists(textId: String): Boolean {
        return textRepository.exists(textId)
    }

    @Transactional(readOnly = true)
    override fun get(textId: String): Text? {
        return if (textRepository.exists(textId)) convertToModel(textRepository.findOne(textId), Text::class.java) else null
    }

    @Transactional
    override fun save(text: Text) {
        validate(text)
        textRepository.save(convertToEntity(text, TextEntity::class.java))
    }

    @Transactional
    override fun delete(textId: String) {
        if (Strings.isNullOrEmpty(textId)) {
            return
        }

        if (textRepository.exists(textId)) {
            textRepository.delete(textId)
        }
    }

}
