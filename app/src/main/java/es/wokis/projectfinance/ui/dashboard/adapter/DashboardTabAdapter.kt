package es.wokis.projectfinance.ui.dashboard.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.wokis.projectfinance.ui.dashboard.fragment.DashboardTabFragment
import es.wokis.projectfinance.ui.dashboard.fragment.DashboardTabFragment.Companion.TAB_TYPE

class DashboardTabAdapter(
    private val tabsList: List<String>,
    private val onRecyclerScroll: (scrolling: Boolean) -> Unit,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabsList.size

    override fun createFragment(position: Int): Fragment {
        val tabType = tabsList[position]
        val fragment = DashboardTabFragment().apply {
            setScrollListener(onRecyclerScroll)
            arguments = Bundle().apply {
                putString(TAB_TYPE, tabType)
            }
        }
        return fragment
    }
}