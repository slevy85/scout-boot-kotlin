package scout.boot.kotlin.standard.backend.repository

import java.util.UUID

import scout.boot.kotlin.standard.backend.entity.DocumentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<DocumentEntity, UUID>
