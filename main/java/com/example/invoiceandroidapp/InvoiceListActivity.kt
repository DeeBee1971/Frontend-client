package com.example.invoiceandroidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.invoiceandroidapp.Adapter.InvoiceAdapter
import com.example.invoiceandroidapp.Connection.CrudApp
import com.example.invoiceandroidapp.Connection.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class InvoiceListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var invoiceAdapter: InvoiceAdapter
    private lateinit var retrofit: Retrofit
    val mainScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_list)
        retrofit = RetrofitClient.create()
        val crud = retrofit.create(CrudApp::class.java)
        recyclerView = findViewById<RecyclerView>(R.id.invoiceList_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("user_id",-1)
        Log.i("@invoice", "u Id : $userId ")
        fetchInvoices(crud, userId)

        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        val titleTextView: TextView = findViewById<TextView>(R.id.titlebar)
        toolbar.title = ""
        titleTextView.text = "Dashboard"

        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {

            val intent = Intent(this, AddInvoiceActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchInvoices(crud: CrudApp, userId: Int) {
        mainScope.launch {
            val res = crud.getInvoice(userId)
            if (res.isSuccessful) {
                val invoices = res.body() ?: emptyList()
                Log.i("@Listof","list is : $invoices")
                invoiceAdapter = InvoiceAdapter(this@InvoiceListActivity,invoices)
                recyclerView.adapter = invoiceAdapter
            } else {
                Log.e("InvoiceListActivity", "Error fetching invoices: ${res.code()}")
            }
        }
    }

}