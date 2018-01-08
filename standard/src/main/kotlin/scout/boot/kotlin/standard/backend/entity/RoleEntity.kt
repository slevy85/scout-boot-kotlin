package scout.boot.kotlin.standard.backend.entity

import scout.boot.kotlin.standard.model.Role

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
data class RoleEntity(
        @NotNull @Id override var id: String?,
        @ElementCollection override var permissions: MutableSet<String>) :Role() {


}

