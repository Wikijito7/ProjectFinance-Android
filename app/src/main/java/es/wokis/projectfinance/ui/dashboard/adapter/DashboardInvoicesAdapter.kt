package es.wokis.projectfinance.ui.dashboard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.databinding.RowAdBinding
import es.wokis.projectfinance.databinding.RowDashboardInvoiceBinding
import es.wokis.projectfinance.databinding.RowReactionBinding
import es.wokis.projectfinance.ui.dashboard.vo.InvoiceVO
import es.wokis.projectfinance.utils.RecyclerSwipeHelper
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getCategoryName
import es.wokis.projectfinance.utils.getLocale
import es.wokis.projectfinance.utils.getPrimaryColor
import es.wokis.projectfinance.utils.getTintByContrast
import es.wokis.projectfinance.utils.hide
import es.wokis.projectfinance.utils.setVisible
import es.wokis.projectfinance.utils.show
import java.util.Locale


class DashboardInvoicesAdapter :
    RecyclerView.Adapter<DashboardInvoicesAdapter.BindableViewHolder>() {

    private var invoices: List<InvoiceVO> = emptyList()
    private var onReactionClickListener: (id: Long) -> Unit = {
        // no-op
    }

    private var onClickListener: (id: Long) -> Unit = {
        // no-op
    }

    private var onLongClickListener: (view: View, id: Long) -> Unit = { _, _ ->
        // no-op
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        return if (viewType == INVOICE_TYPE) {
            val binding =
                RowDashboardInvoiceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            InvoiceViewHolder(binding)

        } else {
            val binding =
                RowAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AdViewHolder(binding)
        }

    }

    override fun getItemViewType(position: Int): Int =
        if (invoices[position].type == InvoiceType.AD) AD_TYPE else INVOICE_TYPE


    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.bind(invoice, onClickListener, onReactionClickListener, onLongClickListener)
    }

    override fun getItemCount(): Int = invoices.size

    fun setInvoices(invoices: List<InvoiceVO>) {
        this.invoices = invoices.toList()
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (id: Long) -> Unit) {
        this.onClickListener = listener
    }

    fun setOnReactionClickListener(listener: (id: Long) -> Unit) {
        this.onReactionClickListener = listener
    }

    fun setOnLongClickListener(listener: (view: View, id: Long) -> Unit) {
        this.onLongClickListener = listener
    }

    abstract class BindableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(
            invoice: InvoiceVO,
            onClickListener: (id: Long) -> Unit,
            onReactionClickListener: (id: Long) -> Unit = {},
            onLongClickListener: (view: View, id: Long) -> Unit = { _, _ -> }
        )
    }

    class InvoiceViewHolder(private val binding: RowDashboardInvoiceBinding) :
        BindableViewHolder(binding.root) {
        private val locale: Locale by lazy { binding.root.context.getLocale() }

        override fun bind(
            invoice: InvoiceVO,
            onClickListener: (id: Long) -> Unit,
            onReactionClickListener: (id: Long) -> Unit,
            onLongClickListener: (view: View, id: Long) -> Unit
        ) {
            setUpView(invoice, onReactionClickListener)
            setUpClickListener(
                invoice.id,
                onClickListener,
                onReactionClickListener,
                onLongClickListener
            )
        }

        private fun setUpView(invoice: InvoiceVO, onReactionClickListener: (id: Long) -> Unit) {
            setUpInvoiceInfo(invoice)
            setUpInvoiceIcon(invoice)
            setUpSyncIcon(invoice)
            setUpReactions(invoice, onReactionClickListener)
        }

        private fun setUpReactions(
            invoice: InvoiceVO,
            onReactionClickListener: (id: Long) -> Unit
        ) {
            val context = binding.root.context
            binding.rowDashboardInvoiceContainerReactions.removeAllViews()
            binding.rowDashboardInvoiceContainerReactions.setVisible(invoice.reactions.isNotEmpty()) {
                invoice.reactions.forEach {
                    val reaction = RowReactionBinding.inflate(LayoutInflater.from(context))
                    reaction.root.setOnClickListener {
                        onReactionClickListener(invoice.id)
                    }
                    reaction.rowDashboardLabelEmoji.apply {
                        text = it
                        setOnClickListener {
                            onReactionClickListener(invoice.id)
                        }
                    }
                    binding.rowDashboardInvoiceContainerReactions.addView(reaction.root)
                }
            }
        }

        private fun setUpClickListener(
            id: Long,
            onClickListener: (id: Long) -> Unit,
            onReactionClickListener: (id: Long) -> Unit,
            onLongClickListener: (view: View, id: Long) -> Unit
        ) {
            binding.root.setOnClickListener {
                onClickListener(id)
            }

            binding.root.setOnLongClickListener {
                onLongClickListener(it, id)
                true
            }

            binding.rowDashboardInvoiceContainerReactions.setOnClickListener {
                onReactionClickListener(id)
            }
        }

        private fun setUpInvoiceInfo(invoice: InvoiceVO) {
            binding.apply {
                val context = root.context
                rowDashboardInvoiceLabelTitle.text = invoice.title
                rowDashboardInvoiceLabelDate.text = invoice.date
                rowDashboardInvoiceLabelCategory.text =
                    context.getCategoryName(invoice.categoryName)
                rowDashboardInvoiceImgCategory.drawable.setTint(Color.parseColor(invoice.categoryColor))
                rowDashboardInvoiceLabelInvoiceQuantity.text = invoice.quantity.asCurrency(locale)
            }
        }

        private fun setUpInvoiceIcon(invoice: InvoiceVO) {
            binding.rowDashboardInvoiceImgInvoiceTypeIcon.apply {
                val categoryColor = Color.parseColor(invoice.categoryColor)
                setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        when (invoice.type) {
                            InvoiceType.EXPENSE -> {
                                R.drawable.ic_expense
                            }

                            else -> R.drawable.ic_deposit
                        },
                        context.theme,
                    )
                )
                background?.setTint(categoryColor)
                drawable?.setTint(getTintByContrast(categoryColor))
            }
        }

        private fun setUpSyncIcon(invoice: InvoiceVO) {
            val context = binding.root.context
            binding.rowDashboardImgCloudSync.apply {
                val backgroundColor = context.theme.getPrimaryColor()
                setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        when {
                            invoice.cloudSync && invoice.updated.not() -> R.drawable.ic_cloud_sync
                            invoice.synchronize.not() -> R.drawable.ic_cloud_never
                            else -> R.drawable.ic_cloud_not_sync
                        },
                        context.theme
                    )
                )
                backgroundColor?.let {
                    background.setTint(it)
                    drawable.setTint(getTintByContrast(it))
                }
            }
        }
    }

    class AdViewHolder(private val binding: RowAdBinding) : BindableViewHolder(binding.root) {

        override fun bind(
            invoice: InvoiceVO,
            onClickListener: (id: Long) -> Unit,
            onReactionClickListener: (id: Long) -> Unit,
            onLongClickListener: (view: View, id: Long) -> Unit
        ) {
            val context = binding.root.context
            AdLoader.Builder(context, context.getString(R.string.admob__native_id)).forNativeAd {
                binding.apply {
                    rowAdLabelAdTitle.text = it.headline
                    rowAdLabelAdDesc.text = it.body
                    it.icon?.drawable?.let {
                        rowAdImageAd.setImageDrawable(it)

                    } ?: rowAdImageAd.hide()
                    rowAdContainerNativeAd.callToActionView = rowAdContainerNativeAd
                    rowAdContainerNativeAd.setNativeAd(it)
                }
            }
                .also {
                    it.withAdListener(object : AdListener() {
                        override fun onAdLoaded() {
                            binding.apply {
                                rowAdContainerNativeAd.show()
                                rowAdProgressLoadingAd.hide()
                                rowAdLabelLoadingAd.hide()
                            }
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            binding.rowAdContainerNativeAd.hide()
                            binding.root.hide()
                        }
                    })
                }
                .build()
                .also {
                    it.loadAd(AdRequest.Builder().build())
                }
            itemView.tag = RecyclerSwipeHelper.TAG_NO_SWIPE
        }
    }

    companion object {
        private const val AD_TYPE = 0
        private const val INVOICE_TYPE = 1
    }
}