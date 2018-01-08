package scout.boot.kotlin.standard.model

import java.util.*
import javax.validation.constraints.Size

/**
 * Documents in the form of named files (byte arrays)
 */
open class Document(open var name: String) : Model<UUID>() {

    init {
        id = UUID.randomUUID()
    }

    @Size(max = CONTENT_SIZE_MAX, message = CONTENT_ERROR_SIZE)
    open var data: ByteArray? = null

    var type: Int = TYPE_OTHER

    val size: Long
        get() = data?.size?.toLong() ?: 0

    constructor() : this("") {
    }

    constructor(name: String, data: ByteArray, type: Int) : this(name) {
        this.name = name
        this.type = type
        this.data = data
    }

    companion object {

        /**
         * Hard size limit for documents: 100MB.
         */
        const val CONTENT_SIZE_MAX = 100 * 1048576
        const val CONTENT_ERROR_SIZE = "ContentErrorSize"

        const val TYPE_OTHER = 0
        const val TYPE_PICTURE = 1
    }

}
