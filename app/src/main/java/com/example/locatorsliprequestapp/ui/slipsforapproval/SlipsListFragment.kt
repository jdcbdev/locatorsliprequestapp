package com.example.locatorsliprequestapp.ui.slipsforapproval

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locatorsliprequestapp.api.ApiClient
import com.example.locatorsliprequestapp.api.RequestBySupervisorListResponse
import com.example.locatorsliprequestapp.api.UpdateStatusResponse
import com.example.locatorsliprequestapp.databinding.FragmentSlipsListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlipsListFragment : Fragment() {

    private var _binding: FragmentSlipsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LocatorSlipForApprovalAdapter
    private var statusFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusFilter = it.getString(ARG_STATUS_FILTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlipsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = LocatorSlipForApprovalAdapter(requireContext(), emptyList(),
            onApprove = { requestId -> updateRequestStatus(requestId, "Approved") },
            onDeny = { requestId -> updateRequestStatus(requestId, "Denied") },
            statusFilter = statusFilter ?: "Pending")
        binding.slipsRecyclerView.adapter = adapter
        binding.slipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadRequests()
        }

        loadRequests()

        return root
    }

    private fun loadRequests() {
        binding.swipeRefreshLayout.isRefreshing = true
        val pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val supervisorId = pref.getInt("empId", 0)

        if (supervisorId != 0) {
            ApiClient.instance.getRequestsBySupervisor(supervisorId, statusFilter ?: "Pending")
                .enqueue(object : Callback<RequestBySupervisorListResponse> {
                    override fun onResponse(
                        call: Call<RequestBySupervisorListResponse>,
                        response: Response<RequestBySupervisorListResponse>
                    ) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        if (response.isSuccessful && response.body()?.success == true) {
                            adapter.updateData(response.body()!!.requestBySupervisorData)
                        } else {
                            Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RequestBySupervisorListResponse>, t: Throwable) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun updateRequestStatus(requestId: Int, status: String) {
        val pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val supervisorId = pref.getInt("empId", 0)
        ApiClient.instance.updateRequestStatus(requestId, status, supervisorId)
            .enqueue(object : Callback<UpdateStatusResponse> {
                override fun onResponse(
                    call: Call<UpdateStatusResponse>,
                    response: Response<UpdateStatusResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Request has been $status", Toast.LENGTH_SHORT).show()
                        loadRequests()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UpdateStatusResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STATUS_FILTER = "status_filter"

        @JvmStatic
        fun newInstance(statusFilter: String) = SlipsListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_STATUS_FILTER, statusFilter)
            }
        }
    }
}