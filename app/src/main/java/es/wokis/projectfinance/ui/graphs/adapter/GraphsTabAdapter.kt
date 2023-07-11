package es.wokis.projectfinance.ui.graphs.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.wokis.projectfinance.ui.graphs.enums.GraphTypeEnum
import es.wokis.projectfinance.ui.graphs.enums.GraphTypeEnum.BAR_GRAPH
import es.wokis.projectfinance.ui.graphs.enums.GraphTypeEnum.PIE_CHART
import es.wokis.projectfinance.ui.graphs.fragment.GraphsFragment.Companion.GRAPH_TAB_TYPE
import es.wokis.projectfinance.ui.graphs.fragment.LineGraphTabFragment
import es.wokis.projectfinance.ui.graphs.fragment.PieGraphTabFragment

class GraphsTabAdapter(
    private val graphTabsList: List<String>,
    private val tabTypeList: List<GraphTypeEnum>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = graphTabsList.size

    override fun createFragment(position: Int): Fragment {
        val graphDataTabType = graphTabsList[position]
        val graphType = tabTypeList[position]
        val fragment = when(graphType) {
            BAR_GRAPH -> LineGraphTabFragment()
            PIE_CHART -> PieGraphTabFragment()
        }.apply {
            arguments = Bundle().apply {
                putString(GRAPH_TAB_TYPE, graphDataTabType)
            }
        }
        return fragment
    }
}