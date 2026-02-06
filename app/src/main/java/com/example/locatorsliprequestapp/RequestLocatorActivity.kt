package com.example.locatorsliprequestapp

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.locatorsliprequestapp.api.AddRequestResponse
import com.example.locatorsliprequestapp.api.ApiClient
import com.example.locatorsliprequestapp.api.EmployeeResponse
import com.example.locatorsliprequestapp.databinding.ActivityRequestLocatorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RequestLocatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestLocatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestLocatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set the back arrow color
        binding.toolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)

        // Set current date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.tvDate.text = currentDate

        // --- CHECK USER SESSION ---
        val pref = getSharedPreferences("session", MODE_PRIVATE)
        val empId = pref.getInt("empId", 0)

        ApiClient.instance.getEmployeeById(empId)
            .enqueue(object : Callback<EmployeeResponse> {

                override fun onResponse(
                    call: Call<EmployeeResponse>,
                    response: Response<EmployeeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val emp = response.body()!!.employee

                        val fullName = "${emp.firstname} ${emp.lastname}"
                        val supervisor = emp.supervisor_name ?: "None"

                        binding.tvName.text = fullName
                        binding.tvSupervisor.text = supervisor
                        binding.tvDept.text = emp.department
                        binding.tvEmployeeCode.text = emp.employee_code

                    } else {
                        var errorMessage = "An unknown error occurred."
                        if (!response.isSuccessful) {
                            errorMessage = "Request failed with code: ${response.code()}"
                        } else if (response.body()?.success == false) {
                            errorMessage = "API returned success false."
                        } else if (response.body() == null) {
                            errorMessage = "Response body is null."
                        }
                        Toast.makeText(this@RequestLocatorActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                    Toast.makeText(this@RequestLocatorActivity, "API call failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })


        // Populate the purpose dropdown
        val purposes = arrayOf("Official", "Personal", "Medical", "Lunch")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, purposes)
        binding.purposeDropdown.setAdapter(adapter)

        binding.submitButton.setOnClickListener {
            val purpose = binding.purposeDropdown.text.toString()
            val destination = binding.destinationEditText.text.toString()

            if (purpose.isNotEmpty() && destination.isNotEmpty()) {
                val pref = getSharedPreferences("session", MODE_PRIVATE)
                val employeeId = pref.getInt("empId", 0)

                if (employeeId != 0) {
                    addRequest(employeeId, purpose, destination)
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addRequest(employeeId: Int, purpose: String, location: String) {
        ApiClient.instance.addRequest(employeeId, purpose, location)
            .enqueue(object : Callback<AddRequestResponse> {
                override fun onResponse(
                    call: Call<AddRequestResponse>,
                    response: Response<AddRequestResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(applicationContext, "Request submitted successfully", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish() // Close the activity
                    } else {
                        Toast.makeText(applicationContext, "Failed to submit request", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddRequestResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}