package vce.nhs.pomodolock.fragments.CompassFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vce.nhs.pomodolock.R
import vce.nhs.pomodolock.utils.Compass
import java.util.*

class CompassAdapter(private var itemList: List<CompassModel>) :
    RecyclerView.Adapter<CompassHolder>() {

    fun updateTimetable(newTimetable: List<CompassModel>) {
        itemList = newTimetable
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompassHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_compass, parent, false)
        return CompassHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompassHolder, position: Int) {
        val currentItem = itemList[position]

        holder.summaryTextView.text = currentItem.summary
        holder.teacherTextView.text = currentItem.teacher
        holder.locationTextView.text = currentItem.location
        "${currentItem.startTime} - ${currentItem.endTime}".also { holder.timeTextView.text = it }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}