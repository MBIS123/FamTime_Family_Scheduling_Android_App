package com.example.famtime.model

class Event {


    var eventTittle:String? = null
    var startTime:String? = null
    var endTime:String? = null
    var description:String? = null
    var oraganiserName:String? = null
    var eventStatus :String ? = null
    var eventDate :String ? = null

    constructor(){}



    constructor(  title:String? , startTime:String?, endTime:String?,description:String? ,name:String? , eventStatus:String?, eventDate:String?){
        this.eventTittle = title
        this.startTime = startTime
        this.endTime = endTime
        this.description = description
        this.oraganiserName = name
        this.eventStatus =  eventStatus
        this.eventDate = eventDate
    }





}