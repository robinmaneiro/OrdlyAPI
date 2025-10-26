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
        return bagRepository.findById(ObjectId(bagId)).getOrElse {
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

    fun deleteBagItem(itemId: String): BagResponse {
        bagRepository.deleteById(ObjectId(itemId))
        return bagRepository.findAll().toBagResponse()
    }

    fun patchBagItem(bagId: String, itemId: String, quantity: Int): BagResponse {
        val itemToUpdate = bagRepository.findById(ObjectId(itemId))
            .getOrElse { throw IllegalArgumentException("Invalid Item ID: $itemId") }
//            ?: return ResponseEntity TODO: Handle error with Either pattern
//                .status(HttpStatus.NOT_FOUND)
//                .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Failed to retrieve item"))

        val updatedTotalPrice = PriceData(
            withTax = itemToUpdate.price.unit.withTax.times(quantity),
            withoutTax = itemToUpdate.price.unit.withoutTax.times(quantity)
        )

        val updatedItem = itemToUpdate.copy(
            quantity = quantity,
            price = itemToUpdate.price.copy(total = updatedTotalPrice)
        )
        bagRepository.save(updatedItem)

        return bagRepository.findAll().toBagResponse()
    }

    fun deleteAllBagItems(): BagResponse {
        bagRepository.deleteAll()
        return bagRepository.findAll().toBagResponse()
    }
}
