package scout.boot.kotlin.standard.backend.entity

import scout.boot.kotlin.standard.backend.entity.converter.LocaleConverter
import scout.boot.kotlin.standard.backend.entity.converter.UuidConverter
import scout.boot.kotlin.standard.model.User
import java.util.*
import javax.persistence.*

@Entity
data class UserEntity(
        @Id @Column(length = User.ID_LENGTH_MAX) var id: String,
        @Column(nullable = false, length = User.NAME_LENGTH_MAX) var firstName: String,
        @Column(length = User.NAME_LENGTH_MAX) var lastName: String?,
        @Column(nullable = false) var passwordHash: String,
        @Column(nullable = false) @Convert(converter = LocaleConverter::class) var locale: Locale,
        @Column @Convert(converter = UuidConverter::class) var pictureId: UUID?,
        @Column var enabled: Boolean,
        @ElementCollection var roles: MutableSet<String>?)
