package com.robinmaneiro.order_kiosk_backend.controller

import com.robinmaneiro.order_kiosk_backend.database.model.BagItem
import com.robinmaneiro.order_kiosk_backend.database.repository.BagRepository
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuProductsRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

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

    data class ErrorResponse(val status: Int, val error: String)

    @PostMapping("/add")
    fun addItemToBag(
        @Valid @RequestBody body: AddToBagRequest
    ): ResponseEntity<Any> {
        if (!ObjectId.isValid(body.productId)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse(400, "Invalid product id"))
        }

        val product = try {
            menuProductsRepository.findByItemId(body.productId).getOrNull()
        } catch (_: Exception) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(500, "Error fetching product"))
        }

        if (product == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(404, "Product not found"))
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
            val updatedProductList = bagRepository.findAll()
            ResponseEntity.status(HttpStatus.CREATED).body(updatedProductList)
        } catch (_: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(500, "Failed to save bag item"))
        }
    }

    @GetMapping("/get")
    fun fetchBagItems(): ResponseEntity<Any> {
        val bagItems = bagRepository.findAll()
        return ResponseEntity.status(HttpStatus.OK).body(bagItems)
    }

    @DeleteMapping("/delete/{productId}")
    fun deleteBagItems(@PathVariable productId: String): ResponseEntity<Any> {
        bagRepository.deleteByProductId(productId)
        val updatedBagItemsList = bagRepository.findAll()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedBagItemsList)
    }
}
