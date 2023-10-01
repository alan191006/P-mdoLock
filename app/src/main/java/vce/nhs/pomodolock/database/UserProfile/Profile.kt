package vce.nhs.pomodolock.database.UserProfile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    val email: String,
    val password: String,
    val salt: String,
    val url: String?
)