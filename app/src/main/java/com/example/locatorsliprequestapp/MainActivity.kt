package com.example.locatorsliprequestapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.locatorsliprequestapp.api.ApiClient
import com.example.locatorsliprequestapp.api.CountEmployeeRequestsResponse
import com.example.locatorsliprequestapp.api.EmployeeResponse
import com.example.locatorsliprequestapp.databinding.ActivityMainBinding
import com.example.locatorsliprequestapp.ui.requestbyemployee.RequestByEmployeeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val addRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            // Refresh the HomeFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
            val requestByEmployeeFragment = navHostFragment?.childFragmentManager?.fragments?.get(0) as? RequestByEmployeeFragment
            requestByEmployeeFragment?.fetchRequests()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- CHECK USER SESSION ---
        val pref = getSharedPreferences("session", MODE_PRIVATE)
        val empId = pref.getInt("empId", 0)



        if (empId == 0) {
            // User not logged in → go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close MainActivity so back button won't return here
            return
        }
        // --- END SESSION CHECK ---

        // Inflate MainActivity layout if user is logged in
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {
            val employeeId = getSharedPreferences("session", MODE_PRIVATE).getInt("empId", 0)
            if (employeeId == 0) {
                Toast.makeText(this, "Cannot verify user.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for pending requests first
            ApiClient.instance.countRequestsByStatus(employeeId, "pending")
                .enqueue(object : Callback<CountEmployeeRequestsResponse> {
                    override fun onResponse(
                        call: Call<CountEmployeeRequestsResponse>,
                        response: Response<CountEmployeeRequestsResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            if (response.body()!!.count > 0) {
                                Toast.makeText(this@MainActivity, "Cant make request while have request not yet completed", Toast.LENGTH_LONG).show()
                            } else {
                                // No pending requests, now check for approved requests
                                ApiClient.instance.countRequestsByStatus(employeeId, "approved")
                                    .enqueue(object : Callback<CountEmployeeRequestsResponse> {
                                        override fun onResponse(
                                            call: Call<CountEmployeeRequestsResponse>,
                                            response: Response<CountEmployeeRequestsResponse>
                                        ) {
                                            if (response.isSuccessful && response.body()?.success == true) {
                                                if (response.body()!!.count > 0) {
                                                    Toast.makeText(this@MainActivity, "Cant make request while have request not yet completed", Toast.LENGTH_LONG).show()
                                                } else {
                                                    // No pending or approved requests, proceed
                                                    val intent = Intent(this@MainActivity, RequestLocatorActivity::class.java)
                                                    addRequestLauncher.launch(intent)
                                                }
                                            } else {
                                                Toast.makeText(this@MainActivity, "Failed to check requests status.", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<CountEmployeeRequestsResponse>, t: Throwable) {
                                            Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Failed to check requests status.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<CountEmployeeRequestsResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Fetch and display user info in the navigation header
        updateNavHeader(empId)
    }

    private fun updateNavHeader(employeeId: Int) {
        ApiClient.instance.getEmployeeById(employeeId)
            .enqueue(object : Callback<EmployeeResponse> {
                override fun onResponse(
                    call: Call<EmployeeResponse>,
                    response: Response<EmployeeResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        println(response.body())
                        response.body()?.employeeData?.let { emp ->
                            val headerView = binding.navView.getHeaderView(0)
                            val navHeaderName = headerView.findViewById<TextView>(R.id.navHeaderName)
                            val navHeaderDepartment =
                                headerView.findViewById<TextView>(R.id.navHeaderDepartment)

                            val fullName = "${emp.firstname} ${emp.lastname}"
                            navHeaderName.text = fullName
                            navHeaderDepartment.text = emp.department

                            // --- CHECK USER SESSION ---
                            val pref = getSharedPreferences("session", MODE_PRIVATE)
                            val empRole = pref.getString("role", "")

                            // Show/hide menu item based on role
                            if (empRole != "admin") {
                                val menu = binding.navView.menu
                                menu.findItem(R.id.nav_gallery).isVisible = false
                            }
                        } ?: run {
                            Toast.makeText(this@MainActivity, "Failed to retrieve employee data", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        // Optional: Tint logout icon red
        val logoutItem = menu.findItem(R.id.action_logout)
        logoutItem.icon?.setTint(ContextCompat.getColor(this, R.color.wmsu))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Clear session
                val pref = getSharedPreferences("session", MODE_PRIVATE)
                pref.edit().clear().apply()

                // Go to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
