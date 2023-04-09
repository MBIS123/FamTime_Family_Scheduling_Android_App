package com.example.famtime.Firebase

import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.famtime.model.Chat
import com.example.famtime.model.Event
import com.example.famtime.model.User
import com.example.famtime.ui.activity.LoginActivity
import com.example.famtime.ui.activity.MainActivity
import com.example.famtime.ui.activity.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseClass {
    private var  mDbRef = FirebaseDatabase.getInstance().getReference()

     fun signUp(activity: RegisterActivity,user: User,password:String){
        // login logic
         var mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(user.email!!, password!!)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // code for next screen
                    mAuth.signInWithEmailAndPassword(user.email!!, password)
                        .addOnSuccessListener {
                            val progressDialog = ProgressDialog(activity)
                            progressDialog.setMessage("Registering Account...")
                            progressDialog.setCancelable(false)
                            progressDialog.show()
                            Handler().postDelayed({
                                progressDialog.dismiss()
                                Toast.makeText(activity , "Registration  Successfull!", Toast.LENGTH_SHORT).show()
                                val uid = it.user?.uid ?: ""
                                addUserToDatabase(user, uid);
                                val intent = Intent(activity,LoginActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()
                                }, 1000)
                        }
                        .addOnFailureListener { exception ->
                            // handle sign in failure
                        }
                } else {
                    Toast.makeText(activity, "The email was used by other user.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(activity, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    //  store new user to realtime database
    private fun addUserToDatabase( user:User , uid:String){
        mDbRef.child("user").child(uid).setValue(user)
    }

     fun addEventToDatabase(familyCode:String, event:Event , date:String){
         val query = mDbRef.child("Family").child(familyCode).child("Activity").child(date)
         query.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 val count = snapshot.childrenCount.toInt()
                 val newActivityName = "activity " + (count + 1)
                 val newActivityRef = query.child(newActivityName)
                 newActivityRef.setValue(event)
             }
             override fun onCancelled(error: DatabaseError) {
                 Log.e("TAG", "Failed to count activities: ${error.message}")
             }
         })
     }
    // create the chat record to database
    fun addChatToDatabase(familyCode: String, chat: Chat) {
        val chatRef = mDbRef.child("Family").child(familyCode).child("Chat").push()
        chatRef.setValue(chat)
            .addOnSuccessListener {
                Log.d("TAG", "Chat added to database: $chat")
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Failed to add chat to database: ${e.message}")
            }
    }

    // get user name and family code
    fun getUserData(callback: (String, String,String,String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val userName = snapshot.child("name").getValue(String::class.java) ?: ""
                val familyCode = snapshot.child("familyCode").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val imageURL = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                callback(userName, familyCode,email,imageURL)
            } else {
                callback("", "","","")
            }
        }
    }


    fun getEventStatus(dateString:String, activityIndex: Int, callback: (String) -> Unit){
        getUserData { name, familyCode,email,imageURL ->
        val familyRef =  FirebaseDatabase.getInstance().getReference("Family").child(familyCode)
            .child("Activity").child(dateString).child("activity $activityIndex")

        familyRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val evtStat = snapshot.child("eventStatus").getValue(String::class.java) ?: ""
                callback(evtStat)
            } else {
                callback("")
            }
        }
        }
    }
    // set the event to completed
    fun setEventStatus(dateString: String, activityIndex: Int) {
        getUserData { name, familyCode, email, imageURL ->
            val familyRef = FirebaseDatabase.getInstance().getReference("Family").child(familyCode)
                .child("Activity").child(dateString).child("activity $activityIndex")
            familyRef.child("eventStatus").setValue("Completed")
        }
    }

}