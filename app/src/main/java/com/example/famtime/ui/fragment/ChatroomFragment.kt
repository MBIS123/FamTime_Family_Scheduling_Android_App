package com.example.famtime.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.databinding.FragmentChatroomBinding
import com.example.famtime.databinding.FragmentProfileBinding
import com.example.famtime.model.Chat
import com.example.famtime.utils.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatroomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatroomFragment : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatArrayList: ArrayList<Chat>
    private lateinit var binding:FragmentChatroomBinding
    private lateinit var firebaseClass: FirebaseClass

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
        binding = FragmentChatroomBinding.inflate(inflater, container, false)
        activity?.title = "Our Chatroom"

        // display from the end
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        chatRecyclerView = binding.chatRecycleView
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.setHasFixedSize(true)
        firebaseClass = FirebaseClass()

        chatArrayList =  arrayListOf<Chat>()

        val chatAdapter = ChatAdapter(chatArrayList)
        chatRecyclerView.adapter = chatAdapter
        getChatData(chatAdapter)

        binding.btnSendMessage.setOnClickListener {
            firebaseClass.getUserData { userName,familyCode,_,_ ->
                val chat = Chat(userName,binding.tvMessageBox.text.toString())
                firebaseClass.addChatToDatabase(familyCode,chat)
                clear()
            }
        }
        return binding.root
    }

    private fun getChatData(adapter: RecyclerView.Adapter<*>){
        firebaseClass.getUserData{ _,familyCode,_,_ ->
            dbref = FirebaseDatabase.getInstance().getReference("Family").child(familyCode).child("Chat")

            dbref.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (chatSnapShot in snapshot.children){
                            val chat = chatSnapShot.getValue(Chat::class.java)
                            chatArrayList.add(chat!!)
                        }
                        chatRecyclerView.adapter = ChatAdapter(chatArrayList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
    fun clear() {
        chatArrayList.clear()
        chatRecyclerView.adapter?.notifyDataSetChanged()
        binding.tvMessageBox.text.clear()

        // Hide the keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}