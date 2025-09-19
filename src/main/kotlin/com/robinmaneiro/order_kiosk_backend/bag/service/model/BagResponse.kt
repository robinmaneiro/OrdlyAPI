package com.robinmaneiro.order_kiosk_backend.bag.service.model


data class BagResponse(
    val totalCost: PriceModel,
    val items: List<BagItem>
)

data class BagItem(
    val itemId: String,
    val productId: String,
    val quantity: Int,
    val title: String,
    val description: String,
    val price: ItemPrice
)

data class ItemPrice(
    val unit: PriceModel,
    val total: PriceModel
)

data class PriceModel(
    val currencyCode: String = "GBP",
    val withTax: Int,
    val withoutTax: Int,
    val tax: TaxModel = TaxModel()
)

data class TaxModel(
    val vat: VatModel = VatModel()
)

data class VatModel(
    val amount: Int = 0,
    val rate: Int = 0
)

data class ErrorResponse(val status: Int, val error: String)