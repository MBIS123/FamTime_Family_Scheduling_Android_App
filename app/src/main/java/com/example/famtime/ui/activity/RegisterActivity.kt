package com.example.famtime.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var btnRegister: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef:DatabaseReference
    private lateinit var textView: TextView
    private val firebaseClass = FirebaseClass()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()  //  retrieves an instance of the FirebaseAuth class.
        edtEmail = findViewById(R.id.edt_Email)
        edtPassword = findViewById(R.id.edt_Password)
        edtName = findViewById(R.id.edt_userName)
        btnRegister = findViewById(R.id.btnSignIn)
        textView = findViewById(R.id.tv_backLogIn)

        // make the LogIn text clickable
        val ss: SpannableString = SpannableString("Back To LogIn")
        val clickableSpan: ClickableSpan = object : ClickableSpan(){
            override fun onClick(textView: View) {
                val intent = Intent(this@RegisterActivity , LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.RED)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val boldSpan = StyleSpan(Typeface.BOLD)
        ss.setSpan(boldSpan, 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textView = findViewById<TextView>(R.id.tv_backLogIn)
        textView.text=ss
        textView.movementMethod = LinkMovementMethod.getInstance()


        // Get Family code from the previous activity - CreateFamilyGroup
        val familyCode =  intent?.getStringExtra("familyCode")


        btnRegister.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Registration")
            builder.setMessage("Are you sure you want to register?")
            builder.setPositiveButton("Yes") { _, _ ->
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword =
                    findViewById<EditText>(R.id.edt_confirmPassword).text.toString()
                val name = edtName.text.toString()
                if (familyCode != null) {
                    if(checkValid(email, password, confirmPassword, name)){
                        familyCode?.let { firebaseClass.signUp(this@RegisterActivity, User(name,email, it,"https://firebasestorage.googleapis.com/v0/b/famtime-7a3de.appspot.com/o/Profile%2FprofileIconNew.png?alt=media&token=4c98bb45-df69-4d26-913b-6b21939ca55e"),password) };
                    }
                    else
                        Toast.makeText(this@RegisterActivity , "Registration Not Successfull!", Toast.LENGTH_LONG).show()
                };
            }
            builder.setNegativeButton("No"){ dialog,_-> dialog.dismiss() }
            val dialog =  builder.create()
            dialog.show()
        }
    }
     fun checkValid(email:String , password:String,confirmPassword :String , name:String):Boolean{
        if (name.isEmpty() || name.isBlank()) {
            Toast.makeText(this@RegisterActivity , "Please enter your username", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || email.isBlank()) {
            Toast.makeText(this@RegisterActivity , "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.isBlank()) {
            Toast.makeText(this@RegisterActivity , "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }
         if(confirmPassword.isEmpty() || confirmPassword.isBlank()){
             Toast.makeText(this@RegisterActivity , "Please enter your confirm password", Toast.LENGTH_SHORT).show()
             return false }
        if(password!= confirmPassword){
            Toast.makeText(this@RegisterActivity , "Please ensure your passwords match with confirm password", Toast.LENGTH_SHORT).show()
            return false }
        if(password.length < 6)
        {Toast.makeText(this@RegisterActivity , "Please set password that is at least 6 characters long!", Toast.LENGTH_SHORT).show()
        return false}
        // if all input is valid
        return true
    }




}