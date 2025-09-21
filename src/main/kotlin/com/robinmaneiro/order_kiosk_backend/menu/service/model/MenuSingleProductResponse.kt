package com.robinmaneiro.order_kiosk_backend.menu.service.model

import com.robinmaneiro.order_kiosk_backend.bag.service.model.TaxModel

data class MenuSingleProductResponse(
    val id: String,
    val title: String,
    val description: String,
    val price: PriceData,
    val categories: List<String>
)

data class PriceData(
    val currencyCode: String = "GBP",
    val withTax: Int,
    val withoutTax: Int,
    val tax: TaxModel = TaxModel()
)