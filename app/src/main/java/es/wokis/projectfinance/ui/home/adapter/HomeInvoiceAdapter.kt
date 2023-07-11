package es.wokis.projectfinance.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.wokis.projectfinance.R
import es.wokis.projectfinance.data.bo.invoice.InvoiceType
import es.wokis.projectfinance.databinding.RowInvoiceBinding
import es.wokis.projectfinance.ui.home.vo.HomeInvoiceVO
import es.wokis.projectfinance.utils.asCurrency
import es.wokis.projectfinance.utils.getLocale
import java.util.Locale

class HomeInvoiceAdapter : RecyclerView.Adapter<HomeInvoiceAdapter.ViewHolder>() {

    private var invoices: List<HomeInvoiceVO> = emptyList()
    private var listener: (id: Long) -> Unit = {
        // no-op
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = invoices[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int = invoices.size

    fun setInvoices(invoices: List<HomeInvoiceVO>) {
        this.invoices = invoices.toList()
        notifyDataSetChanged()
    }

    fun setListener(listener: (id: Long) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(private val binding: RowInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val locale: Locale by lazy { binding.root.context.getLocale() }

        fun bind(invoice: HomeInvoiceVO, listener: (id: Long) -> Unit) {
            setUpView(invoice)
            setUpClickListener(invoice.id, listener)
        }

        private fun setUpView(invoice: HomeInvoiceVO) {
            setUpInvoiceInfo(invoice)
            setUpInvoiceIcon(invoice)
        }

        private fun setUpInvoiceInfo(invoice: HomeInvoiceVO) {
            binding.rowInvoiceLabelTitle.text = invoice.title
            binding.rowInvoiceLabelDate.text = invoice.date
            binding.rowInvoiceLabelInvoiceQuantity.text = invoice.quantity.asCurrency(locale)
        }

        private fun setUpInvoiceIcon(invoice: HomeInvoiceVO) {
            Glide
                .with(binding.rowInvoiceImgInvoiceTypeIcon)
                .load(
                    when (invoice.type) {
                        InvoiceType.EXPENSE -> {
                            R.drawable.ic_expense
                        }

                        else -> R.drawable.ic_deposit
                    }
                ).into(binding.rowInvoiceImgInvoiceTypeIcon)
        }

        private fun setUpClickListener(id: Long, listener: (id: Long) -> Unit) {
            binding.root.setOnClickListener {
                listener(id)
            }
        }
    }
}