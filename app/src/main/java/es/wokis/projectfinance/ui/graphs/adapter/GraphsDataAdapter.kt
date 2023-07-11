package es.wokis.projectfinance.ui.graphs.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.wokis.projectfinance.databinding.RowGraphDataBinding
import es.wokis.projectfinance.ui.graphs.vo.GraphDataVO
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.setVisible

class GraphsDataAdapter : ListAdapter<GraphDataVO, GraphsDataAdapter.ViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowGraphDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: RowGraphDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GraphDataVO) {
            val context = binding.root.context
            binding.graphDataLabelTitle.text = item.title
            binding.graphDataLabelAmount.text = item.amount.asCurrency(context.getLocale())
            binding.graphDataLabelDesc.setVisible(item.description.isNullOrEmpty().not()) {
                binding.graphDataLabelDesc.text = item.description
            }
            binding.graphDataImgIcon.setVisible(item.categoryColor.isNullOrEmpty().not()) {
                binding.graphDataImgIcon.drawable.setTint(Color.parseColor(item.categoryColor))
            }
        }

    }


    class DiffUtils : DiffUtil.ItemCallback<GraphDataVO>() {

        override fun areItemsTheSame(oldItem: GraphDataVO, newItem: GraphDataVO): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: GraphDataVO, newItem: GraphDataVO): Boolean =
            oldItem == newItem
    }
}