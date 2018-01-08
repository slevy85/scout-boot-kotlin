package scout.boot.kotlin.standard.model.service

import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.User

interface UserService : ValidatorService<User> {

    /**
     * Returns all available Users.
     */
    val all: List<User>

    /**
     * Checks if the user specified by the provided user id exists.
     *
     * @param userId
     * @return True if the specified user exits, false otherwise.
     */
    fun exists(userId: String): Boolean

    /**
     * Returns the user object for the user specified by the provided user id.
     *
     * @param userId
     * @return
     */
    operator fun get(userId: String): User?

    /**
     * Returns the set of roles for the user specified by the provided user id.
     *
     * @param userId
     */
    fun getRoles(userId: String): Set<Role>

    /**
     * Returns the picture associated with the user specified by the provided user id. If the specified user does not
     * exist or does not have a picture associated null is returned.
     *
     * @param userId
     * @return The picture in the form of the byte array (corresponds to the respective image file content)
     */
    fun getPicture(userId: String): Document?

    /**
     * Persists the provided user, including associated roles. To save the user picture use method [setPicture].
     *
     * @param user
     */
    fun save(user: User?)

    /**
     * Persists the picture for the user specified by the provided user id.
     *
     * @param userId
     * @param picture
     * The picture in the form of a document
     */
    fun setPicture(userId: String, picture: Document?)
}
