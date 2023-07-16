package vn.nhh.aid.fragments.SessionFragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.nhh.aid.R

class SessionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ans: TextView = itemView.findViewById(R.id.mcAnswer)

    fun getAns(): TextView {
        return ans
    }
}