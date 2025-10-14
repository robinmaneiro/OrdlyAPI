package com.robinmaneiro.order_kiosk_backend.bag.controller

import com.robinmaneiro.order_kiosk_backend.bag.service.BagService
import com.robinmaneiro.order_kiosk_backend.bag.service.model.BagResponse
import com.robinmaneiro.order_kiosk_backend.bag.service.model.ErrorResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.apache.coyote.Response
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
//        if (!ObjectId.isValid(requestBody.productId)) {
//            return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid product id"))
//        }

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
