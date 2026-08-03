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
import java.time.Instant
import kotlin.jvm.optionals.getOrElse

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
                price = dbBagItem.price,
                imageUrl = dbBagItem.imageUrl
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
            createdAt = createdAt,
            items = items
        )
    }

    private fun getBagOrThrow(bagId: String): DbBag {
        return bagRepository.findById(ObjectId(bagId)).getOrElse {
            throw IllegalArgumentException("Invalid Bag ID: $bagId")
        }
    }

    fun createBag(bagId: ObjectId, bagType: String) {
        bagRepository.save(
            DbBag(
                id = bagId,
                bagType = bagType,
                createdAt = Instant.now(),
                items = emptyList()
            )
        )
    }

    fun fetchBag(bagId: String): BagResponse {
        return getBagOrThrow(bagId)
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
            price = bagItemPrice,
            imageUrl = product.imageUrl
        )

        val updatedBag = getBagOrThrow(bagId).run {
            copy(
                items = items + productToInsert
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

        val updatedBag = bag.copy(
            items = bag.items.filterNot { it.id == ObjectId(itemId) }
        )

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

    fun mergeBags(sourceBagId: String, targetBagId: String): BagResponse {
        val sourceBag = getBagOrThrow(sourceBagId)
        val targetBag = getBagOrThrow(targetBagId)
        val mergedMap = mutableMapOf<String, DbBagItem>()

        sourceBag.items.forEach { product ->
            mergedMap[product.productId] = product
        }

        targetBag.items.forEach { product ->
            val existingProduct = mergedMap[product.productId]

            if (existingProduct != null) { // Product exists, combine quantities.
                mergedMap[product.productId] = existingProduct.copy(
                    quantity = existingProduct.quantity + product.quantity
                )
            } else {
                mergedMap[product.productId] = product
            }
        }

        val mergedBag = targetBag.copy(items = mergedMap.values.toList())
        bagRepository.save(mergedBag)

        // TODO: In the future, remove the guest bag when successful - leaving it for now we have a reference.

        return mergedBag.toBagResponse()
    }
}
