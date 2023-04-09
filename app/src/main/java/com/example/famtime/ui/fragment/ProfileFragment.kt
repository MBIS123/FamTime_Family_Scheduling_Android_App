package com.example.famtime.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.databinding.FragmentProfileBinding
import com.example.famtime.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {



    private lateinit var storage:FirebaseStorage
    private lateinit var binding:FragmentProfileBinding
    private lateinit var dbref: FirebaseDatabase
    private lateinit var mAuth:FirebaseAuth

    private var selectedImg:Uri? = null


    // initialize object for firebaseclass to interact with firebase
    private val firebaseClass = FirebaseClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        activity?.title = "My Profile"


        firebaseClass.getUserData { name, code,email,imageURL ->
           binding.edtUserRename.setText(name)
            binding.tvFamilyCode.text = " Family Code :  $code"
            Glide.with(this)
                .load(imageURL)
                .into(binding.imgProfilePic)
        }
        // firebase instance
        dbref = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        binding.imgProfilePic.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent , 1)
        }

        binding.btnUpdateProfile.setOnClickListener{
            binding.btnUpdateProfile.setOnClickListener{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Update Profile")
                builder.setMessage("Are you sure you want to update your profile?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    // Show a progress dialog while updating the profile
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setMessage("Updating profile...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    Handler().postDelayed({
                        progressDialog.dismiss()
                        firebaseClass.getUserData { name, code,email,imageURL ->
                            val userNewName = binding.edtUserRename.text.toString()
                            if (userNewName == null){
                                Toast.makeText(requireContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show()
                            }
                            else if(userNewName != name){
                                uploadName()
                                updateProfile()
                                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                updateProfile()
                                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            }

                        }
                    },1000)


                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()

                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
        return binding.root
    }

    private fun updateProfile() {
        firebaseClass.getUserData {name, code,email,imageURL->
            val reference = storage.reference.child("Profile").child(Date().time.toString())
            selectedImg?.let {
                //update profile pic and name
                reference.putFile(it).addOnCompleteListener{
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener { task->
                            uploadInfo(task.toString())
                        }
                    }
                }
            }
        }
    }

    private fun uploadInfo(imgUrl:String){
            mAuth.uid?.let {
                dbref.reference.child("user").child(it).child("imageUrl")
                    .setValue(imgUrl)

        }
    }

    private fun uploadName(){
            mAuth.uid?.let {
                dbref.reference.child("user").child(it).child("name")
                    .setValue(binding.edtUserRename.text.toString())
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            if(data.data != null){
                selectedImg =  data.data!!
                binding.imgProfilePic.setImageURI(selectedImg)
            }
        }
    }



}