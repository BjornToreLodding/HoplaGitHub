package com.example.hopla

object UserSession {
    var token: String = ""
    var userId: String = ""
    var email: String = ""
    var name: String = ""
    var alias: String = ""
    var profilePictureURL: String = ""

    fun clear() {
        token = ""
        userId = ""
        email = ""
        name = ""
        alias = ""
        profilePictureURL = ""
    }
}