package scout.boot.kotlin.standard.model.service

import scout.boot.kotlin.standard.model.Document
import java.util.UUID

interface DocumentService : ValidatorService<Document> {

    /**
     * Returns all available documents.
     */
    val all: List<Document>

    /**
     * Returns true if a document with the provided id exists. Returns false otherwise.
     */
    fun exists(id: UUID): Boolean

    /**
     * Returns the document specified by the provided id. If no such document exists, null is returned.
     */
    operator fun get(id: UUID): Document?

    /**
     * Persists the provided document.
     */
    fun save(document: Document)
}
