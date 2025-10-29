package com.robinmaneiro.order_kiosk_backend.bag.service

import com.robinmaneiro.order_kiosk_backend.bag.controller.BagController
import com.robinmaneiro.order_kiosk_backend.bag.database.BagRepository
import com.robinmaneiro.order_kiosk_backend.bag.database.model.DbBag
import com.robinmaneiro.order_kiosk_backend.bag.database.model.DbBagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagItem
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagResponse
import com.robinmaneiro.order_kiosk_backend.bag.service.model.ItemPrice
import com.robinmaneiro.order_kiosk_backend.bag.service.model.PriceData
import com.robinmaneiro.order_kiosk_backend.menu.database.MenuProductsRepository
import com.robinmaneiro.order_kiosk_backend.util.errorhandling.ProductNotFoundException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
class BagService(
    private val bagRepository: BagRepository,
    private val productsRepository: MenuProductsRepository
) {

    private fun DbBag.toBagResponse(): BagResponse {
        val items = items.map { dbBagItem ->
            BagItem(
                itemId = dbBagItem.id.toHexString(),
                productId = dbBagItem.productId,
                quantity = dbBagItem.quantity,
                title = dbBagItem.title,
                description = dbBagItem.description,
                price = dbBagItem.price
            )
        }

        val totalCost = PriceData(
            withTax = items.sumOf { it.price.total.withTax },
            withoutTax = items.sumOf { it.price.total.withoutTax }
        )

        val itemCount = items.sumOf { it.quantity }

        return BagResponse(
            totalCost = totalCost,
            itemCount = itemCount,
            items = items
        )
    }

    private fun getBagOrThrow(bagId: String): DbBag {
        return bagRepository.getBagByBagId(bagId).getOrElse {
            throw IllegalArgumentException("Invalid Bag ID: $bagId")
        }
    }

    fun fetchBag(bagId: String): BagResponse {
        return bagRepository
            .findById(ObjectId(bagId))
            .getOrElse { throw IllegalArgumentException("Invalid Bag ID: $bagId") }
            .toBagResponse()
    }

    fun addItemToBag(bagId: String, requestBody: BagController.AddToBagRequest): BagResponse {

        val product = productsRepository.findByItemId(requestBody.productId).getOrElse {
            throw ProductNotFoundException(requestBody.productId)
        }

        val totalPrice = product.price.times(requestBody.quantity)
        val bagItemPrice = ItemPrice(
            unit = PriceData(
                withTax = product.price,
                withoutTax = product.price
            ),
            total = PriceData(
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

        val updatedBag = getBagOrThrow(bagId).also { bag ->
            bag.copy(
                items = bag.items + productToInsert
            )
        }

        bagRepository.save(updatedBag)
        return getBagOrThrow(bagId).toBagResponse()
    }

    fun deleteBagItem(bagId: String, itemId: String): BagResponse {
        val bag = getBagOrThrow(bagId)

        if (bag.items.none { it.id == ObjectId(itemId) }) {
            throw IllegalArgumentException("Invalid item id: $itemId")
        }

        val updatedBag = bag.also { bag ->
            bag.copy(
                items = bag.items.filterNot { it.id == ObjectId(itemId) }
            )
        }

        bagRepository.save(updatedBag)
        return getBagOrThrow(bagId).toBagResponse()
    }

    fun patchBagItem(bagId: String, itemId: String, quantity: Int): BagResponse {
        if (getBagOrThrow(bagId).items.none { it.id.toHexString() == itemId }) {
           throw IllegalArgumentException("Invalid Item ID: $itemId")
        }


        val updatedItems = getBagOrThrow(bagId).items.map { itemToUpdate ->
            return@map if (itemToUpdate.id.toHexString() == itemId) {
                val updatedTotalPrice = PriceData(
                    withTax = itemToUpdate.price.unit.withTax.times(quantity),
                    withoutTax = itemToUpdate.price.unit.withoutTax.times(quantity)
                )

                itemToUpdate.copy(
                    quantity = quantity,
                    price = itemToUpdate.price.copy(total = updatedTotalPrice)
                )
            } else {
                itemToUpdate
            }
        }


        bagRepository.save(getBagOrThrow(bagId).copy(items = updatedItems))

        return getBagOrThrow(bagId).toBagResponse()
    }

    fun deleteAllBagItems(bagId: String): BagResponse {
        val bag = getBagOrThrow(bagId)
        val updatedBag = bag.copy(items = emptyList())
        bagRepository.save(updatedBag)
        return getBagOrThrow(bagId).toBagResponse()
    }
}
