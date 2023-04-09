package com.example.famtime.utils

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.Event

class TodayActivityAdapter(private val todayActivityList: ArrayList<Event>):RecyclerView.Adapter<TodayActivityAdapter.MyViewHolder>(){

    private val firebaseClass = FirebaseClass()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayActivityAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.today_activity_card,parent,false)
        return TodayActivityAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodayActivityAdapter.MyViewHolder, position: Int) {
        val currentItem = todayActivityList[position]
        holder.eventTittle.text = currentItem.eventTittle
        holder.eventStartTime.text = currentItem.startTime
        holder.eventEndTime.text = currentItem.endTime
        val eventDate = currentItem.eventDate
        val itemPosition = holder.adapterPosition
        if (eventDate != null) {
            firebaseClass.getEventStatus(eventDate, itemPosition+1) { eventStatus ->
                if(eventStatus =="Completed"){
                    holder.eventTittle.apply {
                        text = currentItem.eventTittle
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    holder.eventStartTime.apply {
                        text = currentItem.startTime
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    holder.eventEndTime.apply {
                        text = currentItem.endTime
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Add strike-through style
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }
            }
        }



    }


    override fun getItemCount(): Int {
        return todayActivityList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val eventTittle:TextView = itemView.findViewById(R.id.tv_eventTittle)
        val eventStartTime = itemView.findViewById<TextView>(R.id.tv_startTime)
        val eventEndTime = itemView.findViewById<TextView>(R.id.tv_endTime)

    }


}