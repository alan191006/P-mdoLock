package vce.nhs.pomodolock.database.UserProfile

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import vce.nhs.pomodolock.database.TodoFragment.TodoDao

@Database(entities = [Profile::class], version = 3)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}