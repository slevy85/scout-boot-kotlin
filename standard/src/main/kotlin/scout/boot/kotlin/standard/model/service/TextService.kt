package scout.boot.kotlin.standard.model.service

import scout.boot.kotlin.standard.model.Text
import java.util.Locale

interface TextService : ValidatorService<Text> {

    /**
     * Returns all available translated texts.
     */
    val all: List<Text>

    /**
     * Returns all available translated texts for the provided text key.
     */
    fun getAll(key: String): List<Text>

    /**
     * Returns all available translated texts for the provided locale.
     */
    fun getAll(locale: Locale?): List<Text>

    /**
     * Returns true if a translated text with the provided id exists. Returns false otherwise.
     */
    fun exists(textId: String): Boolean

    /**
     * Returns the translated text specified by the provided id. If no such text exists, null is returned.
     */
    operator fun get(textId: String): Text?

    /**
     * Persists the provided translated text.
     */
    fun save(text: Text)

    /**
     * Delete the text entry with the provided text id.
     */
    fun delete(textId: String)

}
