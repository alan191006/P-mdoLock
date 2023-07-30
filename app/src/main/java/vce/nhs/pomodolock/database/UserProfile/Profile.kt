package vce.nhs.pomodolock.database.UserProfile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey val id: Long,
    val name: String,
    val age: Int,
    // Add other profile properties as needed
)