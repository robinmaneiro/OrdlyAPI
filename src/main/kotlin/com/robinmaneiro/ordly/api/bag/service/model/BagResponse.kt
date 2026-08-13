package com.robinmaneiro.ordly.api.bag.service.model

import java.time.Instant


data class BagResponse(
    val createdAt: Instant,
    val totalCost: PriceData,
    val itemCount: Int,
    val items: List<BagItem>
)

data class BagItem(
    val itemId: String,
    val productId: String,
    val quantity: Int,
    val title: String,
    val description: String,
    val price: ItemPrice,
    val imageUrl: String? = null
)

data class ItemPrice(
    val unit: PriceData,
    val total: PriceData
)

data class PriceData(
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
