package com.example.invoiceandroidapp.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.invoiceandroidapp.R
import com.example.invoiceandroidapp.EditInvoiceActivity
import com.example.invoiceandroidapp.SingleInvoiceActivity
import com.example.invoiceandroidapp.data.Invoice

class InvoiceAdapter(private val context: Context,private val invoices: List<Invoice>) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val clientNameTextView: TextView = itemView.findViewById(R.id.tv_client_name)
        val amountTextView: TextView = itemView.findViewById(R.id.tv_amount)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        val editImageView: ImageView = itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_invoice_item, parent, false)
        return InvoiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val currentItem = invoices[position]
        holder.clientNameTextView.text = currentItem.clientName
        val textamount = "INR.${currentItem.amount}"
        holder.amountTextView.text = textamount
        holder.dateTextView.text = currentItem.invoiceDate
        holder.itemView.setOnClickListener {
            val inid = currentItem.invoiceId
            val sharedPref =context.getSharedPreferences("invoice_data", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val invoiceId = inid
            editor.putInt("invoice_id", invoiceId)
            editor.apply()
            val intent = Intent(context, SingleInvoiceActivity::class.java)
            ContextCompat.startActivity(context, intent ,null)
        }

        holder.editImageView.setOnClickListener {


            val sharedPref =context.getSharedPreferences("invoice_data", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val invoiceId = currentItem.invoiceId
            editor.putInt("invoice_id", invoiceId)
            editor.apply()
            val intent = Intent(context, EditInvoiceActivity::class.java)
            ContextCompat.startActivity(context,intent,null)

        }
    }

    override fun getItemCount() = invoices.size

}

class TransactionItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount) {
            outRect.bottom = spaceHeight
        }
    }
}