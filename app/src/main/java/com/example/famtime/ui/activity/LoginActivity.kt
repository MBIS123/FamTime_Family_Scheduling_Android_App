package com.example.famtime.ui.activity

import android.app.ProgressDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.famtime.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.Context


class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var textView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()



        textView=findViewById(R.id.txtv_Register)
        val ss: SpannableString = SpannableString("Don't have an Account?  Register here!")

        val clickableSpan: ClickableSpan = object : ClickableSpan(){
            override fun onClick(textView: View) {
                val intent = Intent(this@LoginActivity , CreateFamGroupActivity::class.java)
                startActivity(intent)

            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.RED)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val boldSpan = StyleSpan(Typeface.BOLD)
        ss.setSpan(boldSpan, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textView = findViewById<TextView>(R.id.txtv_Register)
        textView.text=ss
        textView.movementMethod = LinkMovementMethod.getInstance()
        edtEmail = findViewById(R.id.edt_Email)
        edtPassword = findViewById(R.id.edt_Password)
        btnLogin = findViewById(R.id.btnLogin)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)

        // save login details
        sharedPreferences = getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        if (savedEmail != null && savedPassword != null) {
            edtEmail.setText(savedEmail)
            edtPassword.setText(savedPassword)
            rememberMeCheckBox.isChecked = true
            login(savedEmail,savedPassword)
        }

        btnLogin.setOnClickListener{
            val email = edtEmail.text.toString()
            val password =edtPassword.text.toString()
            if(checkValid(email,password)){
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Logging in...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                Handler().postDelayed({
                    login(email, password);
                    progressDialog.dismiss()
                }, 500)
            }
        }


    }
    private fun checkValid(email:String , password:String):Boolean{
        if (email.isEmpty() || email.isBlank()) {
            Toast.makeText(this@LoginActivity , "Please enter your email!", Toast.LENGTH_LONG).show()
            return false
        }
        if (password.isEmpty() || password.isBlank()) {
            Toast.makeText(this@LoginActivity , "Please enter your password!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun  login(email:String , password:String){
        FirebaseAuth.getInstance().signOut()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Save email and password to shared preferences if checkbox is checked
                    if (rememberMeCheckBox.isChecked) {
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()
                    } else {
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()
                    }
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this@LoginActivity , "Log In Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity , "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }
}