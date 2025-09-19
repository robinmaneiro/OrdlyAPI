package com.robinmaneiro.order_kiosk_backend.bag.service

import com.robinmaneiro.order_kiosk_backend.bag.database.BagRepository
import com.robinmaneiro.order_kiosk_backend.bag.database.model.DbBagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagResponse
import com.robinmaneiro.order_kiosk_backend.bag.service.model.ItemPrice
import com.robinmaneiro.order_kiosk_backend.bag.service.model.PriceModel
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuProductsRepository
import org.springframework.stereotype.Service

@Service
class BagService(
    private val bagRepository: BagRepository,
    private val menuProductsRepository: MenuProductsRepository
) {
    fun fetchBag(): BagResponse {
        return bagRepository
            .findAll()
            .toBagResponse()
    }

    private fun List<DbBagItem>.toBagResponse(): BagResponse {
            val items = this.map { dbBagItem ->
                val totalPrice = dbBagItem.run { price.times(quantity)}
                val bagItemPrice = ItemPrice(
                    unit = PriceModel(
                        withTax = dbBagItem.price,
                        withoutTax = dbBagItem.price
                    ),
                    total = PriceModel(
                        withTax = totalPrice,
                        withoutTax = totalPrice
                    )
                )

                BagItem(
                    itemId = dbBagItem.id.toHexString(),
                    productId = dbBagItem.productId,
                    quantity = dbBagItem.quantity,
                    title = dbBagItem.title,
                    description = dbBagItem.description,
                    price = bagItemPrice
                )
            }

        val totalCost = PriceModel(
            withTax = items.sumOf { it.price.total.withTax },
            withoutTax = items.sumOf { it.price.total.withoutTax }
        )

        return BagResponse(
            totalCost = totalCost,
            items = items
        )
    }
}