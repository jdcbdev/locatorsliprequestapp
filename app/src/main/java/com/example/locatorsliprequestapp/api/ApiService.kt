package com.example.locatorsliprequestapp.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("pass") pass: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("getRequestsByEmployee.php")
    fun getRequestsByEmployee(
        @Field("employeeId") employeeId: Int
    ): Call<RequestByEmployeeListResponse>

    @FormUrlEncoded
    @POST("addRequest.php")
    fun addRequest(
        @Field("employeeId") employeeId: Int,
        @Field("purpose") purpose: String,
        @Field("location") location: String,
        @Field("details") details: String
    ): Call<AddRequestResponse>

    @FormUrlEncoded
    @POST("getemployeebyid.php")
    fun getEmployeeById(
        @Field("employeeId") employeeId: Int
    ): Call<EmployeeResponse>

    @FormUrlEncoded
    @POST("getRequestsBySupervisor.php")
    fun getRequestsBySupervisor(
        @Field("supervisorId") supervisorId: Int,
        @Field("status") status: String
    ): Call<RequestBySupervisorListResponse>

    @FormUrlEncoded
    @POST("updateRequest.php")
    fun updateRequestStatus(
        @Field("id") requestId: Int,
        @Field("status") status: String,
        @Field("actionby") actionBy: Int
    ): Call<UpdateStatusResponse>
}