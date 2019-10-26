package com.srpark.myapp.home.model.data

import java.io.Serializable

data class UserData(
    val userImage: String? = "",
    val userEmail: String? = "",
    val userName: String? = "",
    val userPassword: String? = ""
) : Serializable
