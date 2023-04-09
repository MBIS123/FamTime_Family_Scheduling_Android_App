package com.example.famtime.model

import android.content.IntentSender

class Chat{
    var senderName: String? = null
    var message: String? = null

    constructor(){}


    constructor(senderID: String? , sentMessage:String?){
        this.senderName = senderID
        this.message =  sentMessage
    }
}