package scout.boot.kotlin.standard.backend.repository

import scout.boot.kotlin.standard.backend.entity.TextEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TextRepository : JpaRepository<TextEntity, String>
