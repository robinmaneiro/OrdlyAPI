package com.robinmaneiro.order_kiosk_backend.bag.controller

import com.robinmaneiro.order_kiosk_backend.bag.service.BagService
import com.robinmaneiro.order_kiosk_backend.util.errorhandling.ErrorResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/basket")
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

    @GetMapping("/{bagId}")
    fun fetchBag(
        @PathVariable bagId: String
    ): ResponseEntity<Any> {
        val response = bagService.fetchBag(bagId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/{bagId}")
    fun addItemToBag(
        @PathVariable bagId: String,
        @Valid @RequestBody requestBody: AddToBagRequest
    ): ResponseEntity<Any> {
        val response = bagService.addItemToBag(bagId, requestBody)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{bagId}/{itemId}")
    fun patchBagItem(
        @PathVariable bagId: String,
        @PathVariable itemId: String,
        @Valid @RequestBody body: UpdateItemRequest
    ): ResponseEntity<Any> {
        val response = bagService.patchBagItem(bagId, itemId, body.quantity)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{bagId}/{itemId}")
    fun deleteBagItem(@PathVariable itemId: String): ResponseEntity<Any> {
        val response = bagService.deleteBagItem(itemId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }

    @DeleteMapping("/{bagId}/all")
    fun deleteAllBagItems(): ResponseEntity<Any> {
        val response = bagService.deleteAllBagItems() //It should return in an empty response
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }
}
