package scout.boot.kotlin.standard.backend.entity

import scout.boot.kotlin.standard.backend.entity.converter.UuidConverter
import scout.boot.kotlin.standard.model.Document
import java.util.*
import javax.persistence.*

@Entity data class DocumentEntity(
        @Id @Convert(converter = UuidConverter::class) override var id: UUID? = UUID.randomUUID(),
        @Column(nullable = false) override var name: String,
        @Lob @Column(length = Document.CONTENT_SIZE_MAX) override var data: ByteArray? = null) : Document(name) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentEntity

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }

}
