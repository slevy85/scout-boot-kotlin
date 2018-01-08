package scout.boot.kotlin.standard.model


import java.util.Locale
import java.util.UUID

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import kotlin.collections.HashSet

/**
 * Application users. Class used for both human and technical (REST API interface) users.
 */
open class User : Model<String> {

  /*  @Size(min = ID_LENGTH_MIN, max = ID_LENGTH_MAX, message = ID_ERROR_LENGTH)
    @Pattern(regexp = ID_PATTERN, message = ID_ERROR_PATTERN)*/
    override var id: String? = null

    @NotNull
    open var passwordHash: String? = null

    @NotNull
    open var locale: Locale? = null

    @NotNull
    @Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = FIRST_NAME_ERROR_LENGTH)
    open var firstName: String? = null

    @Size(max = NAME_LENGTH_MAX, message = LAST_NAME_ERROR_LENGTH)
    open var lastName: String? = null

    open var pictureId: UUID? = null
    open var roles: MutableSet<String> = HashSet()
    open var enabled: Boolean = false

    val isRoot = roles.contains(Role.ROOT_ID)

    constructor()

    constructor(userId: String, firstName: String, passwordHash: String) : super(userId) {
        this.firstName = firstName
        this.passwordHash = passwordHash
        this.locale = LOCALE_DEFAULT
        this.enabled = true
    }

    override fun toString(): String = id + "(" + toDisplayText() + ")"

    fun toDisplayText(): String? {
        return if (lastName == null) {
            firstName
        } else arrayOf(firstName, lastName).joinToString(" ")
    }

    companion object {

       val LOCALE_DEFAULT : Locale = Locale.forLanguageTag("en-US")

       const val ID_LENGTH_MIN = 3
       const val ID_LENGTH_MAX = 32
       const val ID_PATTERN = "^[a-zA-Z0-9\\.]+$"
       const val ID_ERROR_LENGTH = "UserIdErrorLength"
       const val ID_ERROR_PATTERN = "UserIdErrorPattern"

       const val NAME_LENGTH_MIN = 1
       const val NAME_LENGTH_MAX = 64
       const val FIRST_NAME_ERROR_LENGTH = "FirstNameErrorLength"
       const val LAST_NAME_ERROR_LENGTH = "LastNameErrorLength"
    }
}
