package com.example.invoiceandroidapp.data


data class Invoice (

    var invoiceId: Int = 0,
    var userId: Int = 0,
    var clientName: String = "",
    var amount: Double = 0.0,
    var invoiceDate:String = "",
    var description: String = ""

)