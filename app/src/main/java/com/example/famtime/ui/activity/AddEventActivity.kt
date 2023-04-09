package com.example.famtime.ui.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect.Descriptor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat.getLongDateFormat
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.Event
import com.example.famtime.utils.*
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlinx.coroutines.*
import java.text.DateFormat

class AddEventActivity : AppCompatActivity() {

    // Event UI Element
    private lateinit var edtActivityTittle :EditText
    private lateinit var edtActivityDescription :EditText

    private lateinit var btnChooseStartTime: ImageButton
    private lateinit var btnChooseEndTime: ImageButton
   // private lateinit var btnChooseRemindTime: ImageButton

    private lateinit var tvStartTime : TextView
    private lateinit var tvEndTime : TextView
    //private lateinit var tvremindTime:TextView

    private lateinit var imgBtnback: ImageButton
    private lateinit var tvDate :TextView
    private lateinit var btnCreateActivity :Button



    // initialize object for firebaseclass to interact with firebase
    private val firebaseClass = FirebaseClass()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        //Finding ID
        edtActivityTittle=findViewById<EditText>(R.id.edt_ActivityName)
        btnChooseStartTime =  findViewById(R.id.imgBtn_StartTime)
        btnChooseEndTime = findViewById(R.id.imgBtn_EndTime)
       // btnChooseRemindTime = findViewById(R.id.imgBtn_ReminderTime)

        tvStartTime = findViewById(R.id.edt_StartTime)
        tvEndTime = findViewById(R.id.tv_EndTime)
      //  tvremindTime = findViewById(R.id.tv_reminderTime)

        imgBtnback =  findViewById(R.id.imgBtn_Back)
        tvDate = findViewById(R.id.tv_Date)
        btnCreateActivity= findViewById(R.id.btn_CreateActivity)
        edtActivityDescription = findViewById(R.id.edt_Description)


        val dateString = intent.getStringExtra("dateString")
        tvDate.text= dateString


//        createNotificationChannel()
        // button onclick listener
        btnChooseStartTime.setOnClickListener{
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)
            // Create a time picker dialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Set the time on the button text
                    tvStartTime.text = "$hourOfDay:$minute"
                },
                currentHour,
                currentMinute,
                true
            )
            // Show the time picker dialog
            timePickerDialog.show()
        }
        btnChooseEndTime.setOnClickListener{
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            // Create a time picker dialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Set the time on the button text
                    tvEndTime.text = "$hourOfDay:$minute"
                },
                currentHour,
                currentMinute,
                true
            )
            // Show the time picker dialog
            timePickerDialog.show()
        }


        btnCreateActivity.setOnClickListener{
            val tittle = edtActivityTittle.text.toString()
            val startTime = tvStartTime.text.toString()
            val endTime = tvEndTime.text.toString()
            val date =  dateString
            val description = edtActivityDescription.text.toString()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Add Activity")
            builder.setMessage("Are you sure you want to add this $tittle activity to your family schedule?")
            builder.setPositiveButton("Yes") { _, _ ->
                if (tittle != ""){
                    firebaseClass.getUserData { userName, familyCode, email, imageURL ->
                        val event = Event(
                            tittle,
                            startTime,
                            endTime,
                            description,
                            userName,
                            "Incomplete",
                            date
                        )
                        if (date != null) {
                            val progressDialog = ProgressDialog(this)
                            progressDialog.setMessage("Creating Activity...")
                            progressDialog.setCancelable(false)
                            progressDialog.show()

                            // Delay the dismiss of the progress dialog
                            Handler().postDelayed({
                                progressDialog.dismiss()
                                firebaseClass.addEventToDatabase(familyCode, event, date)
                                Toast.makeText(
                                    this@AddEventActivity,
                                    "Activity Created !!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // scheduleNotification()
                                val intent = Intent(this@AddEventActivity, MainActivity::class.java)
                                startActivity(intent)
                            }, 1000) // Delay for 1.2 seconds (1200 milliseconds)
                        } else
                            Toast.makeText(
                                this@AddEventActivity,
                                "date nothing",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
            }else{
                Toast.makeText( this@AddEventActivity,"Please enter the title of activity", Toast.LENGTH_SHORT).show()
            }
        }
            builder.setNegativeButton("No"){ dialog,_-> dialog.dismiss() }
            val dialog =  builder.create()
            dialog.show()


        }
        imgBtnback.setOnClickListener {
            val intent = Intent(this@AddEventActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }



}