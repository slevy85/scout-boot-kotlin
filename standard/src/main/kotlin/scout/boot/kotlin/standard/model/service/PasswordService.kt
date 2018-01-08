package scout.boot.kotlin.standard.model.service

interface PasswordService {

    /**
     * Checks if the provided password matches the implemented policy. The implemented policy does a null check and
     * verifies the password length.
     *
     * @param password
     * @return
     */
    fun matchesPasswordPolicy(password: String?): Boolean

    /**
     * @param passwordPlainAttempt
     * @param passwordHash
     * @return
     */
    fun passwordIsValid(passwordPlainAttempt: String?, passwordHash: String?): Boolean

    /**
     * @param passwordNew
     * @return
     */
    fun calculatePasswordHash(passwordNew: String): String

    companion object {

        const val PASSWORD_LENGTH_MIN = 3
        const val PASSWORD_LENGTH_MAX = 64
    }
}
