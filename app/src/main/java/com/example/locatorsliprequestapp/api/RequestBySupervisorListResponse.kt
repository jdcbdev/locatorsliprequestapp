package com.example.locatorsliprequestapp.api

data class RequestBySupervisorListResponse(
    val success: Boolean,
    val requestBySupervisorData: List<RequestBySupervisorData>
)
