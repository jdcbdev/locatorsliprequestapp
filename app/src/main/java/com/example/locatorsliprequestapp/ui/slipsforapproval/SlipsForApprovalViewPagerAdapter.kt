package com.example.locatorsliprequestapp.ui.slipsforapproval

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SlipsForApprovalViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SlipsListFragment.newInstance("Pending")
            1 -> SlipsListFragment.newInstance("Processed")
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}