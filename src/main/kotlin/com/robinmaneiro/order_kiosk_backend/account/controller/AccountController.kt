package com.robinmaneiro.order_kiosk_backend.account.controller

import com.robinmaneiro.order_kiosk_backend.account.service.AccountService
import com.robinmaneiro.order_kiosk_backend.auth.service.model.AccountDetailsResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService
) {
    @GetMapping("/details")
    fun getAccountDetails(): AccountDetailsResponse {
        return accountService.getAccountDetails()
    }
}
