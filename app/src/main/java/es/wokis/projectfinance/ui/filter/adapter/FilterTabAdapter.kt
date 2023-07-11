package es.wokis.projectfinance.ui.filter.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.wokis.projectfinance.ui.dashboard.fragment.DashboardTabFragment.Companion.TAB_TYPE
import es.wokis.projectfinance.ui.filter.fragment.FilterTabFragment

class FilterTabAdapter(
    private val tabsList: List<String>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabsList.size

    override fun createFragment(position: Int): Fragment {
        val tabType = tabsList[position]
        val fragment = FilterTabFragment().apply {
            arguments = Bundle().apply {
                putString(TAB_TYPE, tabType)
            }
        }
        return fragment
    }
}