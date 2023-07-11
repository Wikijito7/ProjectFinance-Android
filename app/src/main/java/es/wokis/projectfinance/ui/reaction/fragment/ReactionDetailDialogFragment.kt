package es.wokis.projectfinance.ui.reaction.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import es.wokis.projectfinance.R
import es.wokis.projectfinance.databinding.DialogReactionDetailBinding
import es.wokis.projectfinance.ui.base.BaseBottomSheetDialog
import es.wokis.projectfinance.ui.reaction.adapter.ReactionDetailAdapter
import es.wokis.projectfinance.ui.reaction.viewmodel.ReactionDetailViewModel
import es.wokis.projectfinance.utils.showSnackBar

class ReactionDetailDialogFragment : BaseBottomSheetDialog() {

    private val viewModel: ReactionDetailViewModel by viewModels()
    private val saveArgs: ReactionDetailDialogFragmentArgs by navArgs()

    private var binding: DialogReactionDetailBinding? = null
    private var adapter: ReactionDetailAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogReactionDetailBinding.inflate(inflater, container, false)
        setUpDialog()
        extendView()
        setUpAdapter()
        setUpListeners()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getInvoice(saveArgs.id)
        setUpObservers()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setUpListeners() {
        binding?.apply {
            reactionDetailBtnClose.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun setUpObservers() {
        setUpReactionsObserver()
        setUpRemovedReactionObserver()
    }

    private fun setUpRemovedReactionObserver() {
        viewModel.getRemoveReactionLiveData().observe(viewLifecycleOwner) {
            showReactionRemovedSnackBar()
        }
    }

    private fun showReactionRemovedSnackBar() {
        binding?.apply {
            showSnackBar(
                R.string.reaction_detail__reaction_removed,
                R.string.general__revert,
                reactionDetailLabelMainTitle,
                action = {
                    viewModel.reinsertRemovedReaction()
                }
            )
        }
    }

    private fun setUpReactionsObserver() {
        viewModel.getReactionsLiveData().observe(viewLifecycleOwner) {
            it.takeIf { reactions -> reactions.isNotEmpty() }?.let { reactions ->
                adapter?.submitList(reactions)
            } ?: navigateBack()
        }
    }

    private fun setUpAdapter() {
        adapter = ReactionDetailAdapter()
        adapter?.setOnClickListener {
            viewModel.removeReaction(it)
        }
        binding?.reactionDetailListReactions?.adapter = adapter
    }

}