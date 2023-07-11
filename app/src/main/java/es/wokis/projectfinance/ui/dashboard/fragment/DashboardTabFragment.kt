package es.wokis.projectfinance.ui.dashboard.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vanniktech.emoji.EmojiTheming
import com.vanniktech.emoji.search.NoSearchEmoji
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.databinding.PopupReactionBinding
import es.wokis.projectfinance.databinding.RowDashboardTabBinding
import es.wokis.projectfinance.ui.base.BaseFragment
import es.wokis.projectfinance.ui.dashboard.adapter.DashboardInvoicesAdapter
import es.wokis.projectfinance.ui.dashboard.viewmodel.DashboardViewModel
import es.wokis.projectfinance.utils.RecyclerSwipeHelper
import es.wokis.projectfinance.utils.SwipeButton
import es.wokis.projectfinance.utils.getPrimaryColor
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.isTrue
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.show
import es.wokis.projectfinance.utils.showReinsertInvoiceSnackBar
import es.wokis.projectfinance.utils.toDp

class DashboardTabFragment : BaseFragment() {

    private var binding: RowDashboardTabBinding? = null

    private var reactionPopUp: PopupWindow? = null

    private val viewModel by viewModels<DashboardViewModel>()

    private var adapter: DashboardInvoicesAdapter? = null
    private var onRecyclerScroll: (scrolling: Boolean) -> Unit = {
        // no-op
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RowDashboardTabBinding.inflate(inflater, container, false)
        setUpAdapter()
        setUpPopUpWindow()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpEmojiProvider()
        val tabType = arguments?.getString(TAB_TYPE).orEmpty()
        viewModel.getInvoices(tabType)
        viewModel.updateMostUsedReactions()
    }

    override fun onResume() {
        super.onResume()
        setMenu(R.menu.summary_menu)
        setUpMenu()
        showAddInvoiceButton()
        interceptBackPressed {
            binding?.apply {
                when {
                    rowDashboardViewEmojiSelector.isVisible -> hideEmojiSelector()
                    reactionPopUp?.isShowing.isTrue() -> hideReactionPopUp()
                    else -> navigateBack()
                }
            }
        }
    }

    override fun onPause() {
        hideReactionPopUp()
        binding?.rowDashboardViewEmojiSelector.hide()
        super.onPause()
    }

    override fun onDestroyView() {
        reactionPopUp = null
        binding = null
        super.onDestroyView()
    }

    override fun showInvoiceRemovedSnackBar() {
        // no-op
    }

    fun setScrollListener(onRecyclerScroll: (scrolling: Boolean) -> Unit) {
        this.onRecyclerScroll = onRecyclerScroll
    }

    private fun setUpPopUpWindow() {
        reactionPopUp = PopupWindow().apply {
            val binding = PopupReactionBinding.inflate(layoutInflater)
            width = LinearLayout.LayoutParams.WRAP_CONTENT
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            contentView = binding.root
            binding.apply {
                setUpReactionPopUpListeners()
                setUpMostUsedEmojies()
            }
            isFocusable = false
        }
    }

    private fun PopupReactionBinding.setUpMostUsedEmojies() {
        viewModel.getMostUsedReactionsList().takeIf { it.isNotEmpty() }?.let {
            it.getOrNull(0)?.let { reaction ->
                popupReactionLabelFire.text = reaction
            }
            it.getOrNull(1)?.let { reaction ->
                popupReactionLabelConfetti.text = reaction
            }
            it.getOrNull(2)?.let { reaction ->
                popupReactionLabelSad.text = reaction
            }
            it.getOrNull(3)?.let { reaction ->
                popupReactionLabelSurprised.text = reaction
            }
            it.getOrNull(4)?.let { reaction ->
                popupReactionLabelPray.text = reaction
            }
        }
    }

    private fun PopupReactionBinding.setUpReactionPopUpListeners() {
        popupReactionBtnOtherReactions.setOnClickListener {
            showEmojiSelector()
            hideReactionPopUp()
        }
        popupReactionLabelFire.setOnClickListener {
            processEmoji(popupReactionLabelFire.text.toString())
            hideReactionPopUp()
        }
        popupReactionLabelConfetti.setOnClickListener {
            processEmoji(popupReactionLabelConfetti.text.toString())
            hideReactionPopUp()
        }
        popupReactionLabelSad.setOnClickListener {
            processEmoji(popupReactionLabelSad.text.toString())
            hideReactionPopUp()
        }
        popupReactionLabelSurprised.setOnClickListener {
            processEmoji(popupReactionLabelSurprised.text.toString())
            hideReactionPopUp()
        }
        popupReactionLabelPray.setOnClickListener {
            processEmoji(popupReactionLabelPray.text.toString())
            hideReactionPopUp()
        }
    }

    private fun setUpAdapter() {
        adapter = DashboardInvoicesAdapter()
        setUpAdapterListeners()
        binding?.rowDashboardListInvoices?.adapter = adapter
        binding?.rowDashboardListInvoices?.apply {
            adapter = this@DashboardTabFragment.adapter
            setUpSwipeHelper()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    onRecyclerScroll(newState != RecyclerView.SCROLL_STATE_IDLE)
                    hideReactionPopUp()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }
    }

    private fun setUpMenu() {
        getMenu()?.apply {
            children.find {
                it.itemId == R.id.dashboard__menu__sync_now
            }?.isVisible = viewModel.isUserLoggedIn()
        }
    }

    private fun setUpAdapterListeners() {
        adapter?.setOnClickListener {
            navigateTo(DashboardFragmentDirections.actionDashboardToDetail(it))
        }

        adapter?.setOnReactionClickListener {
            navigateTo(DashboardFragmentDirections.actionDashboardToReactionDetail(it))
        }

        adapter?.setOnLongClickListener { view, id ->
            viewModel.setSelectedInvoice(id)
            hideReactionPopUp()
            setUpPopUpWindow()
            reactionPopUp?.showAsDropDown(view, 16.toDp(), -view.height, Gravity.CENTER_VERTICAL)
        }
    }

    private fun hideReactionPopUp() {
        if (reactionPopUp?.isShowing.isTrue()) {
            reactionPopUp?.dismiss()
        }
    }

    private fun setUpSwipeHelper() {
        context?.let {
            val deleteColor =
                ResourcesCompat.getColor(it.resources, R.color.red, it.theme)
            val editColor =
                ResourcesCompat.getColor(it.resources, R.color.light_green, it.theme)
            val editIcon =
                ResourcesCompat.getDrawable(it.resources, R.drawable.ic_edit_swipe, it.theme)
            val deleteIcon =
                ResourcesCompat.getDrawable(it.resources, R.drawable.ic_delete_swipe, it.theme)
            val swipeHelper = ItemTouchHelper(
                RecyclerSwipeHelper(
                    SwipeButton(deleteColor, deleteIcon) { position ->
                        viewModel.deleteInvoice(position)
                    },
                    SwipeButton(editColor, editIcon) { position ->
                        viewModel.editInvoice(position)
                    }
                )
            )
            binding?.apply {
                swipeHelper.attachToRecyclerView(rowDashboardListInvoices)
            }
        }
    }

    private fun setUpObservers() {
        observeInvoices()
        observeDeleteInvoice()
        observeEditInvoice()
    }

    private fun observeEditInvoice() {
        viewModel.getEditInvoiceLiveData().observe(viewLifecycleOwner) {
            navigateTo(
                DashboardFragmentDirections.actionDashboardToAddInvoice(
                    it,
                    GET_FROM_INVOICE
                )
            )
        }
    }

    private fun observeDeleteInvoice() {
        viewModel.getDeleteInvoiceLiveData().observe(viewLifecycleOwner) {
            binding?.apply {
                showReinsertInvoiceSnackBar(rowDashboardListInvoices) {
                    viewModel.reinsertRemovedInvoice()
                }
            }
        }
    }

    private fun observeInvoices() {
        viewModel.getInvoicesLiveData().observe(viewLifecycleOwner) {
            binding?.rowDashboardListInvoices.setVisible(it.isNotEmpty()) {
                adapter?.setInvoices(it)
            }
            binding?.rowDashboardLabelNoContent.setVisible(it.isEmpty())
            viewModel.updateMostUsedReactions()
        }
    }

    private fun setUpEmojiProvider() {
        context?.let {
            binding?.apply {
                val unSelectedCategoryColor =
                    ResourcesCompat.getColor(it.resources, R.color.light_gray, null)
                val selectedCategoryColor = it.theme.getPrimaryColor() ?: return@let
                rowDashboardViewEmojiSelector.setUp(
                    root,
                    editText = rowDashboardInputEmoji,
                    searchEmoji = NoSearchEmoji,
                    theming = EmojiTheming.from(it).copy(
                        primaryColor = unSelectedCategoryColor,
                        secondaryColor = selectedCategoryColor
                    ),
                    onEmojiClickListener = { emoji ->
                        processEmoji(emoji.unicode)
                        hideEmojiSelector()
                    },
                    onEmojiBackspaceClickListener = {
                        // no-op
                    }
                )
            }
        }
    }

    private fun showEmojiSelector() {
        context?.let {
            binding?.apply {
                hideBottomNavigation()
                hideAddInvoiceButton()
                rowDashboardViewEmojiSelector.show()
            }
        }
    }

    private fun RowDashboardTabBinding.hideEmojiSelector() {
        rowDashboardViewEmojiSelector.tearDown()
        rowDashboardViewEmojiSelector.hide()
        showBottomNavigation()
        showAddInvoiceButton()
    }

    private fun processEmoji(unicode: String) {
        viewModel.addReaction(unicode)
        binding?.rowDashboardInputEmoji?.setText(EMPTY_TEXT)
    }

    companion object {
        const val TAB_TYPE = "TAB_TYPE"
        private const val GET_FROM_INVOICE = "GET_FROM_INVOICE"
    }
}