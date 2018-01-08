package scout.boot.kotlin.standard.backend.entity.converter

import java.util.UUID

import javax.persistence.AttributeConverter

class UuidConverter : AttributeConverter<UUID, String> {

    override fun convertToDatabaseColumn(uuid: UUID?): String? {
        return uuid?.toString()

    }

    override fun convertToEntityAttribute(uuid: String?): UUID? {
        return if (uuid == null) {
            null
        } else UUID.fromString(uuid)

    }

}
