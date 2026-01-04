package com.robinmaneiro.order_kiosk_backend.account.controller

import com.robinmaneiro.order_kiosk_backend.account.service.AccountService
import com.robinmaneiro.order_kiosk_backend.account.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.security.token.JwtService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class AccountController(
    private val accountService: AccountService,
    private val jwtService: JwtService
) {
    data class UpdatePhoneRequestBody(
        @field:NotBlank val phoneNumber: String,
        @field:NotBlank val dialingCode: String,
        @field:NotBlank val countryCode: String
    )

    data class UpdateDobRequestBody(
        @field:NotBlank val dateOfBirth: String
    )

    data class UpdateEmailRequestBody(
        @field:Email(message = "Invalid email field")
        val emailAddress: String
    )

    @GetMapping("/me")
    fun getAccountDetails(
        @RequestHeader("Authorization") authorizationHeader: String
    ): AccountDetailsResponse {
        // TODO: Add validation after this
        val userId = jwtService.getUserIdFromToken(authorizationHeader.removePrefix("Bearer "))

        return accountService.getAccountDetails(userId)
    }

    @PatchMapping("/me/phone")
    fun updatePhoneNumber(
        @RequestHeader("Authorization") authorizationHeader: String,
        @Valid @RequestBody updatePhoneBody: UpdatePhoneRequestBody
    ): AccountDetailsResponse {
        val userId = jwtService.getUserIdFromToken(authorizationHeader.removePrefix("Bearer "))
        return accountService.updatePhoneNumber(userId, updatePhoneBody)
    }

    @PatchMapping("/me/dateOfBirth")
    fun updateDateOfBirth(
        @RequestHeader("Authorization") authorizationHeader: String,
        @Valid @RequestBody updateDobBody: UpdateDobRequestBody
    ): AccountDetailsResponse {
        val userId = jwtService.getUserIdFromToken(authorizationHeader.removePrefix("Bearer "))
        return accountService.updateDateOfBirth(userId, updateDobBody)
    }

    @PatchMapping("/me/email")
    fun updateEmailAddress(
        @RequestHeader("Authorization") authorizationHeader: String,
        @Valid @RequestBody updateEmailBody: UpdateEmailRequestBody
    ): AccountDetailsResponse {
        val userId = jwtService.getUserIdFromToken(authorizationHeader.removePrefix("Bearer "))
        return accountService.updateEmailAddress(userId, updateEmailBody)
    }
}
