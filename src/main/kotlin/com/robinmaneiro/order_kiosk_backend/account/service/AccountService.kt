package com.robinmaneiro.order_kiosk_backend.account.service

import com.robinmaneiro.order_kiosk_backend.account.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrElse

@Service
class AccountService(
    private val userRepository: UserRepository
) {
    fun getAccountDetails(userId: String): AccountDetailsResponse {
        val user = userRepository.findById(ObjectId(userId)).getOrElse {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "") // TODO: Adjust
        }

        return AccountDetailsResponse(
            id = user.id.toHexString(),
            title = user.title,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            dateOfBirth = user.dateOfBirth,
            phone = user.phone,
            bagId = user.bagId.toHexString(),
            wishlistId = user.wishlistId.toHexString()
        )
    }
}
