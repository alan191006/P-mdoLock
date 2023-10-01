package vce.nhs.pomodolock.database.UserProfile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProfileDao {
    @Insert
    suspend fun insert(profile: Profile)

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): Profile?

    @Query("SELECT * FROM profiles WHERE email = :email")
    suspend fun getProfileByEmail(email: String): Profile?

    @Query("SELECT * FROM profiles")
    suspend fun getAllUsers(): List<Profile>

    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()
}