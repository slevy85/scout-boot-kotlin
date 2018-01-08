package scout.boot.kotlin.standard.model.service

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

interface ValidatorService<T> {

    /**
     * Returns the a default validator with JSR-303 support. Overwrite this method for custom validation.
     */
    val validator: Validator
        get() {
            val validatorFactory = Validation.buildDefaultValidatorFactory()

            return validatorFactory.validator
        }

    /**
     * Validates the provided object using the validator provided by [getValidator]. If the provided object does not
     * validate a [ConstraintViolationException] is thrown.
     */
    @Throws(ConstraintViolationException::class)
    fun validate(`object`: T) {
        val violations = validator.validate(`object`)

        if (violations.size > 0) {
            throw ConstraintViolationException(violations)
        }
    }
}
