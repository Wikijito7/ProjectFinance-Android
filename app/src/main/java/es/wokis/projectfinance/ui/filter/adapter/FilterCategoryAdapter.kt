package es.wokis.projectfinance.ui.filter.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.wokis.projectfinance.databinding.RowFilterCategoryBinding
import es.wokis.projectfinance.ui.category.vo.CategoryVO
import es.wokis.projectfinance.utils.getCategoryName

class FilterCategoryAdapter : ListAdapter<CategoryVO, FilterCategoryAdapter.ViewHolder>(DiffUtils()) {

    var listener: (category: CategoryVO) -> Unit = {
        // no-op
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowFilterCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    class ViewHolder(private val binding: RowFilterCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryVO, listener: (category: CategoryVO) -> Unit) {
            setUpRow(item)
            setUpClickListener(item, listener)
        }

        private fun setUpClickListener(item: CategoryVO, listener: (category: CategoryVO) -> Unit) {
            binding.root.setOnClickListener {
                listener(item)
            }
        }

        private fun setUpRow(item: CategoryVO) {
            binding.apply {
                categoryLabelName.text = root.context.getCategoryName(item.title)
                categoryImgColor.drawable.setTint(Color.parseColor(item.color))
                categoryRadioCategory.isChecked = item.selected
            }
        }

    }

    class DiffUtils : DiffUtil.ItemCallback<CategoryVO>() {

        override fun areItemsTheSame(oldItem: CategoryVO, newItem: CategoryVO): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CategoryVO, newItem: CategoryVO): Boolean =
            oldItem.id == newItem.id

    }
}