package com.example.famtime.model

class User {
    var name:String? = null
    var email:String? = null
    var uid:String? = null
    var familyCode:String? = null
    var imageUrl :String? = null
    constructor(){}

    constructor(name: String?, img:String?){
        this.name = name
        this.familyCode = familyCode
    }

    constructor(name: String? , email:String?,familyCode :String? , img: String?){
        this.name = name
        this.email = email
        this.familyCode = familyCode
        this.imageUrl = img
    }
}

