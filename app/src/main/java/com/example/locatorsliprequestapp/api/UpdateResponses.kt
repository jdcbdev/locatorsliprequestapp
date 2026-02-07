package com.example.locatorsliprequestapp.api

import com.google.gson.annotations.SerializedName

data class UpdateTimeOutRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class UpdateTimeInRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
