package com.example.locatorsliprequestapp.api

import com.example.locatorsliprequestapp.api.Request

data class RequestListResponse(
    val success: Boolean,
    val requests: List<Request>
)
