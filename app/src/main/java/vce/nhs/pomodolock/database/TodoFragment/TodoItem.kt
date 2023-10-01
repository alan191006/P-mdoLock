package vce.nhs.pomodolock.database.TodoFragment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    val email: String,
    val id: Long = 0,
    val name: String,
    var state: String
)