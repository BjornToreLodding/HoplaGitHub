package com.example.hopla.universalData

// The user session data to be saved in the app
object UserSession {
    var token: String = ""
    var userId: String = ""
    var email: String = ""
    var name: String? = ""
    var alias: String? = ""
    var profilePictureURL: String = ""
    var telephone: String? = null
    var description: String? = ""
    var dob: DateOfBirth? = null
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
        dob = null
        redirect = ""
    }
}
