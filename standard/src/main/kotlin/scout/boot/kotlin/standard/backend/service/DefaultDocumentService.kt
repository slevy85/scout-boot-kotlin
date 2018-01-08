package scout.boot.kotlin.standard.backend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scout.boot.kotlin.standard.backend.entity.DocumentEntity
import scout.boot.kotlin.standard.backend.repository.DocumentRepository
import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.service.DocumentService
import java.util.*

@Service
class DefaultDocumentService(private val documentRepository: DocumentRepository) : DocumentService, MapperService<Document, DocumentEntity> {

    override val all: List<Document>
        @Transactional(readOnly = true)
        get() = documentRepository.findAll()
                .map { document -> convertToModel(document, Document::class.java) }

    @Transactional(readOnly = true)
    override fun exists(id: UUID) = documentRepository.exists(id)


    @Transactional(readOnly = true)
    override fun get(id: UUID) = if (documentRepository.exists(id)) convertToModel(documentRepository.findOne(id), Document::class.java) else null


    @Transactional
    override fun save(document: Document) {
        validate(document)
        documentRepository.save(convertToEntity(document, DocumentEntity::class.java))
    }

}
