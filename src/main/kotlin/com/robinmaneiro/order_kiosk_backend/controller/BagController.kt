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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/bag")
class BagController(
    private val menuProductsRepository: MenuProductsRepository,
    private val bagRepository: BagRepository
) {
    data class AddToBagRequest(
        @field:NotBlank val id: String,
        @field:Min(1) val quantity: Int
    )

    data class ErrorResponse(val status: Int, val error: String)

    @PostMapping("/add")
    fun addItemToBag(
        @Valid @RequestBody body: AddToBagRequest
    ): ResponseEntity<Any> {
        if (!ObjectId.isValid(body.id)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse(400, "Invalid product id"))
        }

        val product = try {
            menuProductsRepository.findByItemId(body.id).getOrNull()
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
            productId = body.id,
            quantity = body.quantity,
            title = product.title,
            description = product.description,
            price = product.price
        )

        return try {
            val saved = bagRepository.save(bagItem)
            ResponseEntity.status(HttpStatus.CREATED).body(saved)
        } catch (_: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(500, "Failed to save bag item"))
        }
    }
}
