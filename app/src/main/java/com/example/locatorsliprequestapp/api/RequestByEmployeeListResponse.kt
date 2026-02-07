package com.example.locatorsliprequestapp.api

data class RequestByEmployeeListResponse(
    val success: Boolean,
    val requestByEmployeeData: List<RequestByEmployeeData>
)
