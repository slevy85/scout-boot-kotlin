package scout.boot.kotlin.standard.backend.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import scout.boot.kotlin.standard.extensions.between
import scout.boot.kotlin.standard.model.service.PasswordService
import javax.inject.Inject

@Service
class DefaultPasswordService : PasswordService {

    @Inject
    lateinit private var passwordEncoder: PasswordEncoder

    override fun matchesPasswordPolicy(password: String?): Boolean {
        if (password == null) {
            return false
        }
        return password.between(PasswordService.PASSWORD_LENGTH_MIN, PasswordService.PASSWORD_LENGTH_MAX)
    }

    override fun passwordIsValid(passwordPlainAttempt: String?, passwordHash: String?): Boolean {
        return if (passwordHash == null || passwordPlainAttempt == null) {
            false
        } else passwordEncoder.matches(passwordPlainAttempt, passwordHash)

    }

    override fun calculatePasswordHash(passwordNew: String): String {
        return passwordEncoder.encode(passwordNew)
    }
}
