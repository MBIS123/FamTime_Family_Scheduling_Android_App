package com.example.famtime.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.famtime.R
import com.example.famtime.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class CreateFamGroupActivity : AppCompatActivity() {


    private lateinit var btnCreateFamilyGrp: Button
    private lateinit var btnBackRegister :ImageButton
    private lateinit var btnJoin : Button
    private lateinit var edt_FamilyCode: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fam_group)
        supportActionBar?.hide()
        // match with xml element
        btnCreateFamilyGrp = findViewById(R.id.btn_CreateFamilyGroup);
        btnBackRegister = findViewById<ImageButton>(R.id.btnBack);
        btnJoin = findViewById(R.id.btn_Join)
        edt_FamilyCode =  findViewById(R.id.edt_familyCode)

        btnBackRegister.setOnClickListener{
            val intent = Intent(this@CreateFamGroupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCreateFamilyGrp.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Create Family Group")
            builder.setMessage("Are you sure you want to create a new family group?")
            builder.setPositiveButton("Yes") { _, _ ->
            val random = Random()
            val familyCode = (random.nextInt(900000) + 100000).toString()
            val intent = Intent(this@CreateFamGroupActivity, RegisterActivity::class.java)
            intent.putExtra("familyCode", familyCode)
            startActivity(intent)
        }
            builder.setNegativeButton("No"){ dialog,_-> dialog.dismiss() }
            val dialog =  builder.create()
            dialog.show()
        }

        btnJoin.setOnClickListener{
            val inputFamCode =  edt_FamilyCode.text.toString()
            validateFamilyCode(inputFamCode) { isValid ->
                if (isValid) {
                    // The input family code is valid, continue with the registration
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Join Family Group")
                    builder.setMessage("Are you sure you want to join the family group of code $inputFamCode?")
                    builder.setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(this@CreateFamGroupActivity, "Family Group Exist !", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateFamGroupActivity, RegisterActivity::class.java)
                    intent.putExtra("familyCode", inputFamCode)
                    startActivity(intent)
                    }
                    builder.setNegativeButton("No"){ dialog,_-> dialog.dismiss() }
                    val dialog =  builder.create()
                    dialog.show()
                } else {
                    // The input family code is invalid, show an error message
                    Toast.makeText(this@CreateFamGroupActivity, "Invalid family code", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun validateFamilyCode(inputFamilyCode: String, callback: (Boolean) -> Unit) {
        val familyCodes = ArrayList<String>()

        // Get a reference to the "user" node in Firebase
        val userRef = FirebaseDatabase.getInstance().getReference("user")

        // Retrieve the family codes from Firebase
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val familyCode = userSnapshot.child("familyCode").getValue(String::class.java)
                    if (familyCode != null) {
                        familyCodes.add(familyCode)
                    }
                }
                // Check if the input family code matches any of the retrieved family codes
                val isValid = familyCodes.contains(inputFamilyCode)
                callback(isValid)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error retrieving family codes from Firebase: ${error.message}")
                callback(false)
            }
        })
    }



}