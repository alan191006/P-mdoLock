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
}