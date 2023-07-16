package vn.nhh.aid

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.room.Room
import kotlinx.coroutines.*
import vn.nhh.aid.database.TodoFragment.TodoDatabase
import vn.nhh.aid.database.TodoFragment.TodoItem

import vn.nhh.aid.databinding.ActivityMainBinding
import vn.nhh.aid.fragments.CompassFragment.CompassFragment
import vn.nhh.aid.fragments.HomeFragment.HomeFragment
import vn.nhh.aid.fragments.SettingsFragment.SettingsFragment
import vn.nhh.aid.fragments.TodoFragment.AddTaskFragment
import vn.nhh.aid.fragments.TodoFragment.TodoAdapter
import vn.nhh.aid.fragments.TodoFragment.TodoFragment

private var shareInstance: MainActivity? = null
class MainActivity : AppCompatActivity(), AddTaskFragment.AddTaskListener,
    TodoFragment.OnTaskAddedListener {
    companion object {
        lateinit var database: TodoDatabase
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        shareInstance = this
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_database"
        ).build()

        // Swipe database (for deving only)
        val todoDao = database.todoDao()

        GlobalScope.launch(Dispatchers.IO) {
            todoDao.deleteAllTasks()
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.todo -> {
                    // replaceFragment(TodoFragment())
                    setupTodoFragment()
                }
                R.id.compass -> {
                    replaceFragment(CompassFragment())
                }
                R.id.settings -> {
                    replaceFragment(SettingsFragment())
                }

            }

            true
        }
    }

    @SuppressLint("CommitTransaction")
    public fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()

    }

    private fun setupTodoFragment() {
        val todoFragment = TodoFragment()
        todoFragment.onTaskAddedListener = this  // Set the listener to MainActivity
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, todoFragment, "TodoFragment")
            .commit()
    }

    override fun onTaskAdded(taskName: String) {
        // Perform any necessary operations when a task is added, such as updating the UI or inserting into the database
        val todoFragment = supportFragmentManager.findFragmentByTag("TodoFragment") as? TodoFragment
        todoFragment?.onTaskAdded(taskName)
    }
}
