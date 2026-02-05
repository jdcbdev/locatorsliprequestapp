package com.example.locatorsliprequestapp.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") user: String,
        @Field("password") pass: String
    ): Call<LoginResponse>
}