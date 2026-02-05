package com.example.locatorsliprequestapp.api

data class LoginResponse(
    val success: Boolean,
    val userId: Int,
    val role: String
)