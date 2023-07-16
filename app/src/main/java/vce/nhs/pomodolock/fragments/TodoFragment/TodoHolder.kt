package vn.nhh.aid.fragments.TodoFragment

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.nhh.aid.R

// import vn.nhh.aid.fragments.TodoFragment.TodoAdapter.MyViewHolder

class TodoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    fun getCheckBox(): CheckBox {
        return checkBox
    }

    fun getCheckBoxText(): String {
        return checkBox.text.toString()
    }
}