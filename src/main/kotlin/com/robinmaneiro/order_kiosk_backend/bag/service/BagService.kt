package com.robinmaneiro.order_kiosk_backend.bag.service

import com.robinmaneiro.order_kiosk_backend.bag.controller.BagController
import com.robinmaneiro.order_kiosk_backend.bag.database.BagRepository
import com.robinmaneiro.order_kiosk_backend.bag.database.model.DbBagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagResponse
import com.robinmaneiro.order_kiosk_backend.bag.service.model.ItemPrice
import com.robinmaneiro.order_kiosk_backend.bag.service.model.PriceModel
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuProductsRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class BagService(
    private val bagRepository: BagRepository,
    private val productsRepository: MenuProductsRepository
) {

    private fun List<DbBagItem>.toBagResponse(): BagResponse {
        val items = this.map { dbBagItem ->
            BagItem(
                itemId = dbBagItem.id.toHexString(),
                productId = dbBagItem.productId,
                quantity = dbBagItem.quantity,
                title = dbBagItem.title,
                description = dbBagItem.description,
                price = dbBagItem.price
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

    fun fetchBag(): BagResponse {
        return bagRepository
            .findAll()
            .toBagResponse()
    }

    fun addItemToBag(requestBody: BagController.AddToBagRequest): BagResponse {
        val product = productsRepository.findByItemId(requestBody.productId).get() // TODO: Handle null

        val totalPrice = product.price.times(requestBody.quantity)
        val bagItemPrice = ItemPrice(
            unit = PriceModel(
                withTax = product.price,
                withoutTax = product.price
            ),
            total = PriceModel(
                withTax = totalPrice,
                withoutTax = totalPrice
            )
        )

        val productToInsert = DbBagItem(
            productId = product.itemId,
            quantity = requestBody.quantity,
            title = product.title,
            description = product.description,
            price = bagItemPrice
        )
        
        bagRepository.save(productToInsert)
        return bagRepository.findAll().toBagResponse()
    }
}