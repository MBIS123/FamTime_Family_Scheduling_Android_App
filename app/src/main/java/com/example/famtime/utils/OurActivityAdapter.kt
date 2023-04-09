package com.example.famtime.utils

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.Event

class OurActivityAdapter(private val activityList: ArrayList<Event>):RecyclerView.Adapter<OurActivityAdapter.MyViewHolder>(){

    // initialize object for firebaseclass to interact with firebase
    private val firebaseClass = FirebaseClass()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.our_activity_cell,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activityList[position]
        holder.title.text = currentItem.eventTittle
        holder.description.text = currentItem.description
        holder.organiserName.text = currentItem.oraganiserName
        holder.startTime.text = currentItem.startTime
        holder.endTime.text = currentItem.endTime
        holder.tvStatus.text=currentItem.eventStatus
        val eventDate = currentItem.eventDate
        val itemPosition = holder.adapterPosition

        if (eventDate != null) {
            firebaseClass.getEventStatus(eventDate, itemPosition+1) { eventStatus ->
                if(eventStatus =="Completed"){
                    holder.title.apply {
                        text = currentItem.eventTittle
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                    }
                    holder.description.apply {
                        text = currentItem.description
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                    }
                    holder.statusBtn.setImageResource(R.drawable.ic_baseline_checked_circle)
                }
            }
        }

        // Set an OnClickListener to the button
        holder.statusBtn.setOnClickListener {
            val currentItem = activityList[itemPosition]
            if (eventDate != null) {
                firebaseClass.getEventStatus(eventDate, itemPosition+1) { eventStatus ->
                    if (eventStatus =="Incomplete"){
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setTitle("Mark activity as complete")
                        builder.setMessage("Are you sure you want to mark this activity as complete?")
                        builder.setPositiveButton("Yes") { dialog, which ->

                        holder.title.apply {
                            text = currentItem.eventTittle
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                        }
                        holder.description.apply {
                            text = currentItem.description
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                        }
                        holder.statusBtn.setImageResource(R.drawable.ic_baseline_checked_circle)
                        firebaseClass.setEventStatus(eventDate,itemPosition+1)
                        clear() }
                        builder.setNegativeButton("Cancel", null)
                        builder.show()


                    }
                }

            }
        }
    }

    fun clear() {
        activityList.clear()
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return activityList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title : TextView =  itemView.findViewById(R.id.tv_ActivityTittle)
        val description : TextView =  itemView.findViewById(R.id.tv_Description)
        val organiserName : TextView =  itemView.findViewById(R.id.tv_organizerName)
        val startTime : TextView =  itemView.findViewById(R.id.tv_startTime)
        val endTime : TextView =  itemView.findViewById(R.id.tv_endTime)
        val statusBtn: ImageButton =  itemView.findViewById(R.id.btnCheck)
        val tvStatus : TextView =  itemView.findViewById(R.id.tv_eventStatus)

    }


}