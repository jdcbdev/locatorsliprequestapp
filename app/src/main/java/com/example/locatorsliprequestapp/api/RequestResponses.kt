package com.example.locatorsliprequestapp.api
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val empId: Int,
    val role: String
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)

data class UpdateTimeOutRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class UpdateTimeInRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class EmployeeResponse(
    val success: Boolean,
    val employeeData: EmployeeData
)

data class UpdateStatusRequestResponse(
    val success: Boolean,
    val message: String
)

data class RequestBySupervisorListResponse(
    val success: Boolean,
    val requestBySupervisorData: List<RequestBySupervisorData>
)

data class RequestByEmployeeListResponse(
    val success: Boolean,
    val requestByEmployeeData: List<RequestByEmployeeData>
)

data class AddRequestResponse(
    val success: Boolean,
    val requestId: Int
)

data class CountEmployeeRequestsResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class ValidateQRCodeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)