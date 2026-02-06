package com.example.locatorsliprequestapp.api

data class Employee(
    val id: Int,
    val employee_code: String,
    val firstname: String,
    val middlename: String?,
    val lastname: String,
    val department: String,
    val supervisor: Int?,
    val supervisor_name: String?
)
