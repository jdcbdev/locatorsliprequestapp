package com.example.locatorsliprequestapp.api

data class Request(
    val purpose: String,
    val location: String,
    val status: String,
    val details: String
)
