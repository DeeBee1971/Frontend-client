package com.example.invoiceandroidapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.invoiceandroidapp.Connection.CrudApp
import com.example.invoiceandroidapp.Connection.RetrofitClient
import com.example.invoiceandroidapp.data.Invoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.Locale

class AddInvoiceActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    val mainScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private lateinit var tvSingleDate: TextView
    private var selectedDate: String? = null
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)
        retrofit = RetrofitClient.create()
        val crud = retrofit.create(CrudApp::class.java)

        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        val titleTextView: TextView = findViewById(R.id.titlebar)
        toolbar.title = ""
        titleTextView.text = "New Invoice"

        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
        val tvSingleDate = findViewById<TextView>(R.id.tv_pick_date)
        this.tvSingleDate = tvSingleDate
        val clickListener = View.OnClickListener {
            showDatePickerDialog()
        }
        linearLayout.setOnClickListener(clickListener)

        findViewById<Button>(R.id.createButton).setOnClickListener{
            mainScope.launch {
                val clientName = findViewById<EditText>(R.id.editTextText).text.toString()
                val amount = findViewById<EditText>(R.id.editTextText2).text.toString()
                val description = findViewById<EditText>(R.id.editTextText3).text.toString()
                val invoiceDate = findViewById<TextView>(R.id.tv_pick_date).text.toString()

                if (clientName.isEmpty() || amount.isEmpty()|| invoiceDate.equals("Select")) {
                    Toast.makeText(this@AddInvoiceActivity, "Fill in all fields", Toast.LENGTH_SHORT).show()
                }
                else if(amount.toDouble()<3000) {
                    Toast.makeText(this@AddInvoiceActivity, "Amount should be than 3000", Toast.LENGTH_SHORT).show()

                }
                else {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate = dateFormat.parse(invoiceDate)
                    val formattedDate = dateFormat.format(parsedDate)


                    val invoice = Invoice (
                        userId =  getSharedPreferences("user_data", MODE_PRIVATE).getInt("user_id",-1),
                        clientName = clientName,
                        amount = amount.toDouble(),
                        invoiceDate = formattedDate,
                        description = description
                    )
                    Log.i("@create list:","$invoice")

                    val response = crud.createInvoice(invoice)

                    if (response.isSuccessful) {
                        Toast.makeText(this@AddInvoiceActivity, "Invoice Created", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@AddInvoiceActivity, InvoiceListActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this@AddInvoiceActivity, "Invoice Creation Error", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }

    private fun showDatePickerDialog() {

        val maxDate = Calendar.getInstance().timeInMillis


        val datePickerDialog = DatePickerDialog(this,
            { view, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                updateDateView(selectedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = maxDate
        datePickerDialog.show()
    }

    fun updateDateView(selectedDate: String) {
        tvSingleDate.text = selectedDate
        this.selectedDate = selectedDate
    }

}