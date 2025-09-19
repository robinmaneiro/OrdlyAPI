package com.robinmaneiro.order_kiosk_backend.bag.controller

import com.robinmaneiro.order_kiosk_backend.bag.service.BagService
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagResponse
import com.robinmaneiro.order_kiosk_backend.bag.service.model.ErrorResponse
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
    private val bagService: BagService
) {
    data class AddToBagRequest(
        @field:NotBlank val productId: String,
        @field:Min(1) val quantity: Int
    )

    data class UpdateItemRequest(
        @field:Min(1) val quantity: Int,
    )

    @GetMapping
    fun fetchBag(): ResponseEntity<Any> {
        val response = bagService.fetchBag()
        return ResponseEntity.status(HttpStatus.OK).body(response) // TODO: Handle errors
    }

    @PostMapping
    fun addItemToBag(
        @Valid @RequestBody requestBody: AddToBagRequest
    ): ResponseEntity<Any> {
        if (!ObjectId.isValid(requestBody.productId)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product id"))
        }

//        val product = try { TODO: Move this to service
//            menuProductsRepository.findByItemId(body.productId).getOrNull()
//        } catch (_: Exception) {
//            return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching product"))
//        }

//        if (product == null) { TODO: Move this to service
//            return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Product not found"))
//        }

        return try {
            val response = bagService.addItemToBag(requestBody)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (_: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to save bag item"))
        }
    }

//    @PatchMapping("/{itemId}")
//    fun patchBagItem(
//        @Valid @RequestBody body: UpdateItemRequest,
//        @PathVariable itemId: String
//    ): ResponseEntity<Any> {
//        val itemToUpdate = bagRepository.findById(ObjectId(itemId)).getOrNull()
//            ?: return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(ErrorResponse(HttpStatus.NOT_FOUND.value(), "Failed to retrieve item"))
//
//        val updatedItem = itemToUpdate.copy(
//            quantity = body.quantity
//        )
//        bagRepository.save(updatedItem)
//
//        val updatedBagItemsList = bagRepository.findAll().toBagItemResponse()
//        val totalPrice = updatedBagItemsList.sumOf { it.price } // TODO: Move this to a method
//
//        val response = BagResponse(
//            items = updatedBagItemsList,
//            totalPrice = totalPrice
//        )
//
//        return ResponseEntity.status(HttpStatus.OK).body(response)
//    }

//    @DeleteMapping("/{itemId}")
//    fun deleteBagItems(@PathVariable itemId: String): ResponseEntity<Any> {
//        bagRepository.deleteById(ObjectId(itemId))
//        val updatedBagItemsList = bagRepository.findAll().toBagItemResponse()
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(updatedBagItemsList)
//    }
}