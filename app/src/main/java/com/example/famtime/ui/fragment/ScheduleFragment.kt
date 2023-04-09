package com.example.famtime.ui.fragment

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.databinding.FragmentProfileBinding
import com.example.famtime.databinding.FragmentScheduleBinding
import com.example.famtime.model.Event
import com.example.famtime.ui.activity.ViewEventActivity
import com.example.famtime.utils.OurActivityAdapter
import com.example.famtime.utils.TodayActivityAdapter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    lateinit var btnAddTask: Button

    private lateinit var binding:FragmentScheduleBinding

    private  lateinit var dbref : DatabaseReference
    private val firebaseClass = FirebaseClass()
    private lateinit var todayActivityRecyclerView: RecyclerView
    private lateinit var todayActivityArrayList: ArrayList<Event>
    private lateinit var dateString:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        activity?.title = "Our Schedule"
        binding.tvTdyActivity.bringToFront()

        todayActivityRecyclerView = binding.todayActivityRecycleView
        todayActivityRecyclerView.layoutManager = LinearLayoutManager(this.context)
        todayActivityRecyclerView.setHasFixedSize(true)

        todayActivityArrayList = arrayListOf<Event>()


        //underline text
        binding.tvTdyActivity.text = "Today's activity"
        binding.tvTdyActivity.paintFlags = binding.tvTdyActivity.paintFlags or Paint.UNDERLINE_TEXT_FLAG




        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
         dateString = dateFormat.format(calendar.time)


        getTodayActivityData()


        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val intent = Intent(requireContext(), ViewEventActivity::class.java)
            intent.putExtra("selectedDate", selectedDate.timeInMillis)
            startActivity(intent)

            val date = Date(selectedDate.timeInMillis)
            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            dateString = dateFormat.format(date)



        }

        return binding.root
    }

    private fun getTodayActivityData(){
        firebaseClass.getUserData {_, familyCode,_,_ ->
            Log.e("tag","today date is :$dateString")
            dbref = FirebaseDatabase.getInstance().getReference("Family").child(familyCode)
                .child("Activity").child(dateString)

            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (todayActivitySnapShot in snapshot.children)
                        {
                            val activity =  todayActivitySnapShot.getValue(Event::class.java)
                            todayActivityArrayList.add(activity!!)
                        }
                        todayActivityRecyclerView.adapter =  TodayActivityAdapter(todayActivityArrayList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Tag", "Today Activity Is Empty")
                }
            })
        }

    }



}



