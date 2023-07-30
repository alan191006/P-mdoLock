package vn.nhh.aid.fragments.TodoFragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.nhh.aid.R
import vn.nhh.aid.database.TodoFragment.TodoItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.nhh.aid.MainActivity.Companion.database

// import vn.nhh.aid.fragments.TodoFragment.TodoAdapter.MyViewHolder

class TodoAdapter(private var todoList: List<TodoItem>) :
    RecyclerView.Adapter<TodoHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateTodoList(newTodoList: List<TodoItem>) {
        todoList = newTodoList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    suspend fun deleteTask(task: TodoItem) {
        withContext(Dispatchers.IO) {
            val todoDao = database.todoDao()
            todoDao.deleteTask(task.id)
            val updatedTodoList = todoDao.getAllTodoItems()
            withContext(Dispatchers.Main) {
                todoList = updatedTodoList
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_recycler_view, parent, false)
        return TodoHolder(view)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        val todo = todoList[position] // Get the corresponding todo item from the list
        holder.getCheckBox().text = todo.name

        // Set the state of the checkbox based on the todo item's state
        holder.getCheckBox().isChecked = todo.state == "completed"

        holder.getCheckBox().setOnCheckedChangeListener(null) // Remove the previous listener to prevent callback collisions

        holder.getCheckBox().setOnCheckedChangeListener { _, isChecked ->
            // Update the state of the todo item in the database
            val newState = if (isChecked) "completed" else "incomplete"
            todo.state = newState

            // Perform the database update asynchronously
            GlobalScope.launch(Dispatchers.IO) {
                val todoDao = database.todoDao()
                todoDao.update(todo)
            }

            // Delete the task if it is marked as completed
            if (isChecked) {
                GlobalScope.launch(Dispatchers.IO) {
                    deleteTask(todo)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return todoList.size
    }
}