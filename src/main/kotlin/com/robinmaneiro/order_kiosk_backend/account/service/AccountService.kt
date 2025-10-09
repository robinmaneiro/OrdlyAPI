package com.robinmaneiro.order_kiosk_backend.account.service

import com.robinmaneiro.order_kiosk_backend.auth.service.model.AccountDetailsResponse
import org.springframework.stereotype.Service

@Service
class AccountService {
    fun getAccountDetails(): AccountDetailsResponse {
        return AccountDetailsResponse(
            id = "123",
            title = "Mr",
            firstName = "Robin",
            lastName = "Maneiro",
            email = "robin@gmail.com",
            dateObBirth = "14-07-1990",
            phone = "777777777"
        )
    }
}
