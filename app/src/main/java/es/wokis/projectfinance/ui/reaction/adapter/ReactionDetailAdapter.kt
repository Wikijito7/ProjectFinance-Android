package es.wokis.projectfinance.ui.reaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.wokis.projectfinance.data.bo.reaction.ReactionBO
import es.wokis.projectfinance.databinding.RowReactionDetailBinding

class ReactionDetailAdapter : ListAdapter<ReactionBO, ReactionDetailAdapter.ViewHolder>(DiffUtils()) {

    private var listener: (reaction: ReactionBO) -> Unit = {
        // no-op
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowReactionDetailBinding =
            RowReactionDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    fun setOnClickListener(listener: (reaction: ReactionBO) -> Unit) {
        this.listener = listener
    }

    class DiffUtils : DiffUtil.ItemCallback<ReactionBO>() {

        override fun areItemsTheSame(oldItem: ReactionBO, newItem: ReactionBO): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ReactionBO, newItem: ReactionBO): Boolean =
            oldItem == newItem

    }

    class ViewHolder(private val binding: RowReactionDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reaction: ReactionBO, listener: (reaction: ReactionBO) -> Unit) {
            setUpReaction(reaction)
            setUpClickListener(reaction, listener)
        }

        private fun setUpClickListener(
            reaction: ReactionBO,
            listener: (reaction: ReactionBO) -> Unit
        ) {
            binding.rowDetailReactionImgDelete.setOnClickListener {
                listener(reaction)
            }
        }

        private fun setUpReaction(reaction: ReactionBO) {
            binding.rowDetailReactionLabelEmoji.text = reaction.unicode
        }
    }

}
