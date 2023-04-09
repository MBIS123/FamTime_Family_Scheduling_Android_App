package com.example.famtime.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputBinding
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.famtime.R
import com.example.famtime.databinding.ActivityMainBinding
import com.example.famtime.ui.fragment.ChatroomFragment
import com.example.famtime.ui.fragment.ProfileFragment
import com.example.famtime.ui.fragment.ScheduleFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "FamTime"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ScheduleFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
               R.id.schedule ->replaceFragment(ScheduleFragment())
                R.id.chatroom ->replaceFragment(ChatroomFragment())
                R.id.profile ->replaceFragment(ProfileFragment())
                else->{
                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTrasaction =  fragmentManager.beginTransaction()
        fragmentTrasaction.replace(R.id.container,fragment)
        fragmentTrasaction.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    // clicked log out menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> {
                FirebaseAuth.getInstance().signOut()
                // removed the saved details for remember me
                val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .remove("email")
                    .remove("password")
                    .apply()

                Toast.makeText(this, "Log Out successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        return true
    }

    private fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

}