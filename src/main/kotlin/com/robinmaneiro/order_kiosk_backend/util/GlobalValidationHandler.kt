package com.robinmaneiro.order_kiosk_backend.util

import com.robinmaneiro.order_kiosk_backend.util.errorhandling.ErrorResponse
import com.robinmaneiro.order_kiosk_backend.util.errorhandling.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalValidationHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(mapOf("errors" to errors))
    }

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND.value())
            .body(ErrorResponse( HttpStatus.NOT_FOUND.value(), e.message.orEmpty()))
    }
}
