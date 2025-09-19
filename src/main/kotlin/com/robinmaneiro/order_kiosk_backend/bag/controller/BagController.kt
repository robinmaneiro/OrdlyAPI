package com.robinmaneiro.order_kiosk_backend.bag.controller

import com.robinmaneiro.order_kiosk_backend.bag.database.BagItem
import com.robinmaneiro.order_kiosk_backend.bag.database.BagRepository
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuProductsRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

// TODO: Review functions and reuse logic
@RestController
@RequestMapping("/basket")
class BagController(
    private val menuProductsRepository: MenuProductsRepository,
    private val bagRepository: BagRepository
) {
    data class AddToBagRequest(
        @field:NotBlank val productId: String,
        @field:Min(1) val quantity: Int
    )

    data class UpdateItemRequest(
        @field:Min(1) val quantity: Int,
    )

    data class BagItemResponse(
        val itemId: String,
        val productId: String,
        val quantity: Int,
        val title: String,
        val description: String,
        val unitPrice: Int,
        val price: Int
    )

    data class BagResponse(

        val items: List<BagItemResponse>
    )

    data class PriceModel(
        val currencyCode: String = "GBP",
        val withTax: Int,
        val withoutTax: Int,
        val tax: TaxModel
    )

    data class TaxModel(
        val vat: VatModel
    )

    data class VatModel(
        val amount: Int = 0,
        val rate: Int = 0
    )

    data class ErrorResponse(val status: Int, val error: String)

    private fun List<BagItem>.toBagItemResponse() = map {
        val price = it.run { price.times(quantity) }
        BagItemResponse(
            itemId = it.id.toHexString(),
            productId = it.productId,
            quantity = it.quantity,
            title = it.title,
            description = it.description,
            unitPrice = it.price,
            price = price
        )
    }

    @GetMapping
    fun fetchBagItems(): ResponseEntity<Any> {
        val bagItems = bagRepository
            .findAll()
            .toBagItemResponse()

        val totalPrice = bagItems.sumOf { it.price }

        val response = BagResponse(
            items = bagItems,
            totalPrice = totalPrice
        )

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping
    fun addItemToBag(
        @Valid @RequestBody body: AddToBagRequest
    ): ResponseEntity<Any> {
        if (!ObjectId.isValid(body.productId)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product id"))
        }

        val product = try {
            menuProductsRepository.findByItemId(body.productId).getOrNull()
        } catch (_: Exception) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching product"))
        }

        if (product == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Product not found"))
        }

        val bagItem = BagItem(
            productId = body.productId,
            quantity = body.quantity,
            title = product.title,
            description = product.description,
            price = product.price
        )

        return try {
            bagRepository.save(bagItem)
            val updatedProductList = bagRepository.findAll().toBagItemResponse()
            ResponseEntity.status(HttpStatus.CREATED).body(updatedProductList)
        } catch (_: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to save bag item"))
        }
    }

    @PatchMapping("/{itemId}")
    fun patchBagItem(
        @Valid @RequestBody body: UpdateItemRequest,
        @PathVariable itemId: String
    ): ResponseEntity<Any> {
        val itemToUpdate = bagRepository.findById(ObjectId(itemId)).getOrNull()
            ?: return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Failed to retrieve item"))

        val updatedItem = itemToUpdate.copy(
            quantity = body.quantity
        )
        bagRepository.save(updatedItem)

        val updatedBagItemsList = bagRepository.findAll().toBagItemResponse()
        val totalPrice = updatedBagItemsList.sumOf { it.price } // TODO: Move this to a method

        val response = BagResponse(
            items = updatedBagItemsList,
            totalPrice = totalPrice
        )

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{itemId}")
    fun deleteBagItems(@PathVariable itemId: String): ResponseEntity<Any> {
        bagRepository.deleteById(ObjectId(itemId))
        val updatedBagItemsList = bagRepository.findAll().toBagItemResponse()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedBagItemsList)
    }
}