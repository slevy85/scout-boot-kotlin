package scout.boot.kotlin.standard.model

import javax.validation.constraints.NotNull

/**
 * Parent class for model entities
 */
open class Model<ID> {

    /**
     * Represents the ID of this object. Must not be null.
     */
    @get:NotNull
    open var id: ID? = null

    constructor()

    constructor(id: ID) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Model<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
