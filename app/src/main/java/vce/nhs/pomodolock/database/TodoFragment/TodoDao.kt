package vce.nhs.pomodolock.database.TodoFragment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Insert
    fun insert(todoItem: TodoItem)

    @Update
    fun update(todoItem: TodoItem)

    @Query("SELECT * FROM todo_items")
    fun getAllTodoItems(): List<TodoItem>

    @Query("SELECT * FROM todo_items WHERE email = email")
    suspend fun getUserTask(email: String): List<TodoItem>


    @Query("DELETE FROM todo_items WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllTasks()
}