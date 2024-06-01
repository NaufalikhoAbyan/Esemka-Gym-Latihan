package com.example.esemkagym.model

import java.io.Serializable

data class LoginData(
    val email: String,
    val password: String
): Serializable
