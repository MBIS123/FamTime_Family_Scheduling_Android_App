package com.example.famtime.ui.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.Event
import com.example.famtime.utils.OurActivityAdapter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ViewEventActivity : AppCompatActivity() {

    private lateinit var btnAddEvent:ImageButton
    private lateinit var tvDate: TextView
    private lateinit var imgBtnBack :ImageButton

    private  lateinit var dbref : DatabaseReference
    private val firebaseClass = FirebaseClass()
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityArrayList: ArrayList<Event>
    private lateinit var dateString: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        // find id in xml file
        imgBtnBack = findViewById(R.id.imgBtn_Back)
        btnAddEvent= findViewById(R.id.btn_AddEvent)
        tvDate = findViewById(R.id.tv_Date)

        // to just underline a text view
        val tv_OurEvent = findViewById<TextView>(R.id.tv_OurActivity)
        tv_OurEvent.setPaintFlags(tv_OurEvent.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        // get date from the schedule
        val selectedDate = intent.getLongExtra("selectedDate", 0) //get the date from schedule module
        val date = Date(selectedDate)
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateString = dateFormat.format(date)
        tvDate.text = dateString


        // button click listener
        btnAddEvent.setOnClickListener {
            val intent =  Intent(this@ViewEventActivity, AddEventActivity::class.java)
            intent.putExtra("dateString", dateString)
            startActivity(intent)
        }
        // go back
        imgBtnBack.setOnClickListener {
            val intent =  Intent(this@ViewEventActivity, MainActivity::class.java)
            startActivity(intent)
        }

        activityRecyclerView =  findViewById(R.id.ourActivityRecyclerView)
        activityRecyclerView.layoutManager = LinearLayoutManager(this)
        activityRecyclerView.setHasFixedSize(true)

        activityArrayList = arrayListOf<Event>()
        getActivityData()


    }
    private fun getActivityData(){
        firebaseClass.getUserData { userName, familyCode,email,imageURL ->

            dbref = FirebaseDatabase.getInstance().getReference("Family").child(familyCode)
                .child("Activity").child(dateString)
            dbref.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (activitySnapShot in snapshot.children)
                        {
                         val activity =  activitySnapShot.getValue(Event::class.java)
                         activityArrayList.add(activity!!)
                        }
                        activityRecyclerView.adapter =  OurActivityAdapter(activityArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            }
        }




    }




