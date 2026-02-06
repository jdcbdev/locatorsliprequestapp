package com.example.locatorsliprequestapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locatorsliprequestapp.api.*
import com.example.locatorsliprequestapp.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.recyclerRequests.layoutManager =
            LinearLayoutManager(requireContext())

        loadRequests()

        return binding.root
    }

    fun loadRequests() {
        // Example: get empId from SharedPreferences
        val prefs = requireActivity()
            .getSharedPreferences("session", 0)
        val empId = prefs.getInt("empId", 0)

        ApiClient.instance.getRequestsByEmployee(empId)
            .enqueue(object : Callback<RequestListResponse> {

                override fun onResponse(
                    call: Call<RequestListResponse>,
                    response: Response<RequestListResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val list = response.body()!!.requests
                        binding.recyclerRequests.adapter = RequestAdapter(list)
                        if (list.isEmpty()) {
                            Toast.makeText(requireContext(), "No requests found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load requests",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<RequestListResponse>,
                    t: Throwable
                ) {
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
