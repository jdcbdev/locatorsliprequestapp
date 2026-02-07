package com.example.locatorsliprequestapp.ui.requestbyemployee

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locatorsliprequestapp.QrScannerActivity
import com.example.locatorsliprequestapp.api.ApiClient
import com.example.locatorsliprequestapp.api.RequestByEmployeeData
import com.example.locatorsliprequestapp.api.RequestByEmployeeListResponse
import com.example.locatorsliprequestapp.api.UpdateTimeInRequestResponse
import com.example.locatorsliprequestapp.api.UpdateTimeOutRequestResponse
import com.example.locatorsliprequestapp.databinding.FragmentRequestByEmployeeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestByEmployeeFragment : Fragment() {

    private var _binding: FragmentRequestByEmployeeBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestByEmployeeAdapter: RequestByEmployeeAdapter
    private var selectedRequest: RequestByEmployeeData? = null

    companion object {
        private const val NULL_DATE = "0000-00-00 00:00:00"
    }

    private val scanQrLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val qrCode = result.data?.getStringExtra("qrCode")
            if (qrCode != null) {
                selectedRequest?.let { request ->
                    if (request.timeOut == null || request.timeOut == NULL_DATE) {
                        updateTimeOutRequest(request.requestId, qrCode)
                    } else {
                        updateTimeInRequest(request.requestId, qrCode)
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(requireContext(), QrScannerActivity::class.java)
            scanQrLauncher.launch(intent)
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestByEmployeeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requestByEmployeeAdapter = RequestByEmployeeAdapter(requireContext(), emptyList()) { request ->
            selectedRequest = request
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(requireContext(), QrScannerActivity::class.java)
                scanQrLauncher.launch(intent)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.recyclerRequests.adapter = requestByEmployeeAdapter
        binding.recyclerRequests.layoutManager = LinearLayoutManager(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchRequests()
        }

        fetchRequests()

        return root
    }

    fun fetchRequests() {
        val sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val empId = sharedPreferences.getInt("empId", -1)

        if (empId != -1) {
            binding.swipeRefreshLayout.isRefreshing = true
            ApiClient.instance.getRequestsByEmployee(empId)
                .enqueue(object : Callback<RequestByEmployeeListResponse> {
                    override fun onResponse(call: Call<RequestByEmployeeListResponse>, response: Response<RequestByEmployeeListResponse>) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        if (response.isSuccessful && response.body() != null) {
                            requestByEmployeeAdapter.updateData(response.body()!!.requestByEmployeeData)
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch requests", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RequestByEmployeeListResponse>, t: Throwable) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", "Error fetching requests", t)
                    }
                })
        } else {
            Toast.makeText(requireContext(), "Employee ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeOutRequest(requestId: Int, exitPoint: String) {
        ApiClient.instance.updateTimeOut(requestId, exitPoint)
            .enqueue(object : Callback<UpdateTimeOutRequestResponse> {
                override fun onResponse(call: Call<UpdateTimeOutRequestResponse>, response: Response<UpdateTimeOutRequestResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Time out updated successfully", Toast.LENGTH_SHORT).show()
                        fetchRequests() // Refresh the list
                    } else {
                        Toast.makeText(requireContext(), "Failed to update time out", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateTimeOutRequestResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateTimeInRequest(requestId: Int, entryPoint: String) {
        ApiClient.instance.updateTimeIn(requestId, entryPoint)
            .enqueue(object : Callback<UpdateTimeInRequestResponse> {
                override fun onResponse(call: Call<UpdateTimeInRequestResponse>, response: Response<UpdateTimeInRequestResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Time in updated successfully", Toast.LENGTH_SHORT).show()
                        fetchRequests() // Refresh the list
                    } else {
                        Toast.makeText(requireContext(), "Failed to update time in", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateTimeInRequestResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun parseTimeOut(timeOut: String?): Date? {
        if (timeOut.isNullOrEmpty()) return null

        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(timeOut)
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
