package com.example.locatorsliprequestapp.ui.slipsforapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.locatorsliprequestapp.databinding.FragmentSlipsForApprovalBinding
import com.google.android.material.tabs.TabLayoutMediator

class SlipsForApprovalFragment : Fragment() {

    private var _binding: FragmentSlipsForApprovalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlipsForApprovalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = SlipsForApprovalViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Approval"
                1 -> "Approved/Denied"
                else -> null
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}