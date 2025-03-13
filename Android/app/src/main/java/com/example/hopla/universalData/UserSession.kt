package com.example.hopla.universalData

object UserSession {
    var token: String = ""
    var userId: String = ""
    var email: String = ""
    var name: String = ""
    var alias: String = ""
    var profilePictureURL: String = ""
    var telephone: Int? = null
    var description: String = ""
    var dob: String = ""
    var redirect: String = ""

    fun clear() {
        token = ""
        userId = ""
        email = ""
        name = ""
        alias = ""
        profilePictureURL = ""
        telephone = null
        description = ""
        dob = ""
        redirect = ""
    }
}