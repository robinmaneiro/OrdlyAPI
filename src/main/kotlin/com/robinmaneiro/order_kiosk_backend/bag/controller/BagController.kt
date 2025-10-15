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
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping
    fun addItemToBag(
        @Valid @RequestBody requestBody: AddToBagRequest
    ): ResponseEntity<Any> {
        val response = bagService.addItemToBag(requestBody)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{itemId}")
    fun patchBagItem(
        @Valid @RequestBody body: UpdateItemRequest,
        @PathVariable itemId: String
    ): ResponseEntity<Any> {
        val response = bagService.patchBagItem(itemId, body.quantity)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{itemId}")
    fun deleteBagItem(@PathVariable itemId: String): ResponseEntity<Any> {
        val response = bagService.deleteBagItem(itemId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }

    @DeleteMapping("all")
    fun deleteAllBagItems(): ResponseEntity<Any> {
        val response = bagService.deleteAllBagItems() //It should return in an empty response
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }
}
