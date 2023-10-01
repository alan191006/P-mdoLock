package vce.nhs.pomodolock.fragments.TodoFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import vce.nhs.pomodolock.MainActivity.Companion.database
import vce.nhs.pomodolock.R
import vce.nhs.pomodolock.database.TodoFragment.TodoItem
import vce.nhs.pomodolock.databinding.FragmentSignupBinding
import vce.nhs.pomodolock.databinding.FragmentTodoBinding
import vce.nhs.pomodolock.fragments.ProfileFragments.LoginFragment
import vce.nhs.pomodolock.utils.SharedPreferencesManager
import java.lang.Runnable

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TodoFragment : Fragment() {
    var onTaskAddedListener: OnTaskAddedListener? = null

    private lateinit var todoAdapter: TodoAdapter // Declare the adapter as a member variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.todoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Check if user is logged in
        val sharedPrefs = SharedPreferencesManager(requireContext())
        if (!sharedPrefs.isLoggedIn()) {
            // User is not logged in, show the login screen
            showLoginScreen()
        } else {
            // User is logged in, display the To-do list
            showTodoList()
        }

        val addButton = binding.addTaskButton
        addButton.setOnClickListener {
            val addTaskFragment = AddTaskFragment.newInstance()
            addTaskFragment.show(childFragmentManager, "AddTaskFragment")
        }

        return view
    }


    private fun showLoginScreen() {
        // Replace the current fragment with a login fragment
        val loginFragment = LoginFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, loginFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showTodoList() {
        val recyclerView = binding.todoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val todoDao = database.todoDao()
        val userEmail = SharedPreferencesManager(requireContext()).getUsername() ?: ""

        // Create an empty adapter
        todoAdapter = TodoAdapter(emptyList())
        recyclerView.adapter = todoAdapter

        // Load tasks for the logged-in user asynchronously and update the adapter
        GlobalScope.launch(Dispatchers.Main) {
            val tasks = withContext(Dispatchers.IO) {
                todoDao.getUserTask(userEmail)
            }
            todoAdapter.updateTodoList(tasks)
        }
    }



    interface OnTaskAddedListener {
        fun onTaskAdded(taskName: String)
    }

    fun onTaskAdded(taskName: String) {
        val userEmail = SharedPreferencesManager(requireContext()).getUsername() ?: ""
        // Perform the necessary operations when a task is added
        // For example, insert the task into the database and update the adapter
        val newTask = TodoItem(email = userEmail, name = taskName, state = "incomplete")

        // Perform the database insertion asynchronously
        val todoDao = database.todoDao()
        GlobalScope.launch(Dispatchers.IO) {
            todoDao.insert(newTask)

            // Retrieve the updated list of todo items from the database
            val updatedTodoList = todoDao.getAllTodoItems()
            val filteredTasks = updatedTodoList.filter { it.email == userEmail }

            // Update the adapter with the new list on the main thread
            withContext(Dispatchers.Main) {
                val todoAdapter = binding.todoRecyclerView.adapter as? TodoAdapter
                todoAdapter?.updateTodoList(filteredTasks)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visible = true
    }

    // Full screen (Hide action + top bar)

    private val hideHandler = Handler(Looper.myLooper()!!)
    private var fullscreenContentControls: View? = null

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    @SuppressLint("ClickableViewAccessibility")
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        fullscreenContentControls?.visibility = View.GONE
        visible = false
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        visible = true
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}