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
import com.example.locatorsliprequestapp.api.RequestListResponse
import com.example.locatorsliprequestapp.databinding.FragmentSlipsForApprovalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlipsForApprovalFragment : Fragment() {

    private var _binding: FragmentSlipsForApprovalBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LocatorSlipAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlipsForApprovalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = LocatorSlipAdapter(requireContext(), emptyList())
        binding.slipsForApprovalRecyclerView.adapter = adapter
        binding.slipsForApprovalRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadRequestsForApproval()

        return root
    }

    private fun loadRequestsForApproval() {
        val pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val supervisorId = pref.getInt("empId", 0)

        if (supervisorId != 0) {
            ApiClient.instance.getRequestsBySupervisor(supervisorId)
                .enqueue(object : Callback<RequestListResponse> {
                    override fun onResponse(
                        call: Call<RequestListResponse>,
                        response: Response<RequestListResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            adapter.updateData(response.body()!!.requests)
                        } else {
                            Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RequestListResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
