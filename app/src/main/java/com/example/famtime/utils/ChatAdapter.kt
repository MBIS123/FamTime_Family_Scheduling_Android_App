package com.example.famtime.utils

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.famtime.Firebase.FirebaseClass
import com.example.famtime.R
import com.example.famtime.model.Chat
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val chatList:ArrayList<Chat>):RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    // initialize object for firebaseclass to interact with firebase
    private val firebaseClass = FirebaseClass()
    // Map to associate each username with a unique color
    private val usernameColorMap = hashMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item,parent,false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentitem = chatList[position]
        val username = currentitem.senderName

        // set the text color based on the username
        if (usernameColorMap.containsKey(username)) {
            holder.userName.setTextColor(usernameColorMap[username]!!)
        } else {
            // create a new random color for this username
            val color = generateRandomColor()
            usernameColorMap[username.toString()] = color
            holder.userName.setTextColor(color)
        }
        holder.userName.apply {
            text = currentitem.senderName
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        holder.userMessage.text = currentitem.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
    private fun generateRandomColor(): Int {
        val random = Random()
        return 0xff000000.toInt() or random.nextInt(0x00ffffff)
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userName : TextView =  itemView.findViewById(R.id.tv_senderName)
        val userMessage : TextView =  itemView.findViewById(R.id.tv_textMessage)
    }
}