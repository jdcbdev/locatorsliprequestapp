package com.example.locatorsliprequestapp.api

import com.google.gson.annotations.SerializedName

data class RequestByEmployeeData(
    @SerializedName("requestId")
    val requestId: Int,
    @SerializedName("employeeId")
    val employeeId: Int,
    val purpose: String,
    val location: String,
    val details: String?,
    @SerializedName("requestDate")
    val requestDate: String,
    @SerializedName("timeOut")
    val timeOut: String?,
    @SerializedName("timeIn")
    val timeIn: String?,
    val exitpoint: String?,
    val entrypoint: String?,
    val status: String,
    @SerializedName("actionBy")
    val actionBy: Int?,
)
