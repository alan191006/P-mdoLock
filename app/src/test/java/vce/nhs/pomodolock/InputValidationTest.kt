package vce.nhs.pomodolock

import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.Test

internal class InputValidationTest {

    @Test
    fun isValidInput() {
        // Test valid input
        val validEmail = "test@gmail.com"
        val validPassword = "password"
        val validConfirmPassword = "password"
        Assertions.assertTrue(
            vce.nhs.pomodolock.utils.isValidInput(
                validEmail,
                validPassword,
                validConfirmPassword
            )
        )

        // Test invalid password
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                validEmail,
                validPassword,
                ""
            )
        )

        // Test invalid email (missing "@")
        val invalidEmailNoAt = "testgmail.com"
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                invalidEmailNoAt,
                validPassword,
                validConfirmPassword
            )
        )

        // Test invalid email (invalid domain)
        val invalidEmailInvalidDomain = "test@invalid"
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                invalidEmailInvalidDomain,
                validPassword,
                validConfirmPassword
            )
        )

        // Test empty email
        val emptyEmail = ""
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                emptyEmail,
                validPassword,
                validConfirmPassword
            )
        )

        // Test empty password
        val emptyPassword = "test@gmail.com"
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                validEmail,
                emptyPassword,
                validConfirmPassword
            )
        )

        // Test password mismatch
        val passwordMismatch = "test@gmail.com"
        val confirmPasswordMismatch = "password123"
        Assertions.assertFalse(
            vce.nhs.pomodolock.utils.isValidInput(
                validEmail,
                passwordMismatch,
                confirmPasswordMismatch
            )
        )
    }
}