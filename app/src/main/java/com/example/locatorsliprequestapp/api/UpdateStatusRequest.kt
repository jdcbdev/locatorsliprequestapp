package com.example.locatorsliprequestapp.api

data class UpdateStatusRequest(
    val requestId: Int,
    val status: String,
    val actionBy: String
)
