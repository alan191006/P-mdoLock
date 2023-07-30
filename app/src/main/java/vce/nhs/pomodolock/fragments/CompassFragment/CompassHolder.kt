package vce.nhs.pomodolock.fragments.CompassFragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vce.nhs.pomodolock.R

class CompassHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val summaryTextView: TextView = itemView.findViewById(R.id.summaryTextView)
    val teacherTextView: TextView = itemView.findViewById(R.id.teacherTextView)
    val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
}