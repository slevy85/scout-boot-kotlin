package scout.boot.kotlin.standard.backend.entity

import scout.boot.kotlin.standard.model.Text

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TextEntity(
        @Id @Column(length = Text.ID_LENGTH_MAX) override var id: String?,
        @Column(length = Text.TEXT_LENGTH_MAX) override var text: String?) : Text(){
    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = super.hashCode()
}
