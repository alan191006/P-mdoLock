package vce.nhs.pomodolock.fragments.TodoFragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vce.nhs.pomodolock.R

class AddTaskFragment : DialogFragment() {

    interface AddTaskListener {
        fun onTaskAdded(taskName: String)
    }

    private var listener: AddTaskListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_task, null)

        val editTextTask = dialogView.findViewById<EditText>(R.id.editTextTask)
        val buttonAdd = dialogView.findViewById<Button>(R.id.buttonAdd)

        builder.setView(dialogView)

        val dialog = builder.create()

        // Set a transparent background for the dialog window
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setOnShowListener {
            buttonAdd.setOnClickListener {
                val taskName = editTextTask.text.toString().trim()
                if (taskName.isNotEmpty()) {
                    listener?.onTaskAdded(taskName)
                    dialog.dismiss()
                } else {
                    editTextTask.error = getString(R.string.task_name_required)
                }
            }
        }

        return dialog
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as AddTaskListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AddTaskListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(): AddTaskFragment {
            return AddTaskFragment()
        }
    }
}
