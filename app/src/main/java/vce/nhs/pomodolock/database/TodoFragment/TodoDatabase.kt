package vce.nhs.pomodolock.database.TodoFragment

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoItem::class], version = 4)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}