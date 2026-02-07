package com.example.locatorsliprequestapp.api

import com.google.gson.annotations.SerializedName

data class RequestBySupervisorData(
    @SerializedName("id")
    val requestId: Int,
    val firstname: String,
    val lastname: String,
    val department: String,
    @SerializedName("requestDate")
    val requestDate: String,
    val purpose: String,
    val location: String,
    val details: String,
    val status: String,
    @SerializedName("timeOut")
    val timeOut: String?,
    @SerializedName("timeIn")
    val timeIn: String?,
    val exitpoint: String?,
    val entrypoint: String?
)
