package com.example.locatorsliprequestapp.ui.requestbyemployee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locatorsliprequestapp.api.*
import com.example.locatorsliprequestapp.databinding.FragmentRequestByEmployeeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestByEmployeeFragment : Fragment() {

    private var _binding: FragmentRequestByEmployeeBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestByEmployeeAdapter: RequestByEmployeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRequestByEmployeeBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.recyclerRequests.layoutManager =
            LinearLayoutManager(requireContext())
        requestByEmployeeAdapter = RequestByEmployeeAdapter(requireContext(), emptyList())
        binding.recyclerRequests.adapter = requestByEmployeeAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadRequests()
        }

        loadRequests()

        return binding.root
    }

    fun loadRequests() {
        // Example: get empId from SharedPreferences
        val prefs = requireActivity()
            .getSharedPreferences("session", 0)
        val empId = prefs.getInt("empId", 0)

        binding.swipeRefreshLayout.isRefreshing = true
        ApiClient.instance.getRequestsByEmployee(empId)
            .enqueue(object : Callback<RequestByEmployeeListResponse> {

                override fun onResponse(
                    call: Call<RequestByEmployeeListResponse>,
                    response: Response<RequestByEmployeeListResponse>
                ) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (response.isSuccessful && response.body()?.success == true) {
                        val list = response.body()?.requestByEmployeeData

                        if (list != null) {
                            requestByEmployeeAdapter.updateData(list)
                            if (list.isEmpty()) {
                                Toast.makeText(requireContext(), "No requests found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "No requests found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No requests found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<RequestByEmployeeListResponse>,
                    t: Throwable
                ) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
