package vce.nhs.pomodolock.utils

fun isValidInput(email: String, password: String, confirmPassword: String): Boolean {
    // Check if the email is not empty and contains the "@" symbol
    if (email.isNotEmpty() && email.contains("@")) {
        val parts = email.split("@")
        if (parts.size == 2) {
            // Split the email into 2 halves, e.g., test@gmail.com to test and gmail.com
            val localPart = parts[0]
            val domainPart = parts[1]

            // Check if the local part is not empty, and the domain part is a valid domain
            if (localPart.isNotEmpty() && isValidDomain(domainPart)) {
                // Check password
                return password.isNotEmpty() && password == confirmPassword
            }
        }
    }

    return false
}

fun isValidDomain(domain: String): Boolean {
    // Use a regex to validate the domain
    val domainRegex = Regex("""^[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
    return domainRegex.matches(domain)
}