package com.robinmaneiro.order_kiosk_backend.account.service

import com.robinmaneiro.order_kiosk_backend.account.controller.AccountController
import com.robinmaneiro.order_kiosk_backend.account.service.model.AccountDetailsResponse
import com.robinmaneiro.order_kiosk_backend.auth.service.AuthService
import com.robinmaneiro.order_kiosk_backend.database.model.User
import com.robinmaneiro.order_kiosk_backend.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.update
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.jvm.optionals.getOrElse

@Service
class AccountService(
    private val userRepository: UserRepository
) {

    private fun User.toAccountDetailsResponse() = AccountDetailsResponse(
        id = id.toHexString(),
        title = title,
        firstName = firstName,
        lastName = lastName,
        email = email,
        dateOfBirth = dateOfBirth,
        phone = phone,
        bagId = bagId.toHexString(),
        wishlistId = wishlistId.toHexString()
    )


    fun getAccountDetails(userId: String): AccountDetailsResponse {
        val user = userRepository.findById(ObjectId(userId)).getOrElse {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "") // TODO: Adjust
        }

        return user.toAccountDetailsResponse()
    }

    // TODO: Rename parameters

    fun updatePhoneNumber(userId: String, phoneRequestBody: AccountController.UpdatePhoneRequestBody): AccountDetailsResponse {
        val user = userRepository.findById(ObjectId(userId)).getOrElse {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "") // TODO: Adjust
        }

        val updatedUser = user.copy(
            phone = phoneRequestBody.phoneNumber // TODO: Update to get rich object
        )

        userRepository.save(updatedUser)

        return updatedUser.toAccountDetailsResponse()
    }

    fun updateDateOfBirth(userId: String, updateDobRequestBody: AccountController.UpdateDobRequestBody): AccountDetailsResponse {
        val user = userRepository.findById(ObjectId(userId)).getOrElse {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "") // TODO: Adjust
        }

        val updatedUser = user.copy(
            dateOfBirth = updateDobRequestBody.dateOfBirth
        )

        userRepository.save(updatedUser)

        return updatedUser.toAccountDetailsResponse()
    }

    fun updateEmailAddress(userId: String, updateEmailRequestBody: AccountController.UpdateEmailRequestBody): AccountDetailsResponse {
        val user = userRepository.findById(ObjectId(userId)).getOrElse {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "") // TODO: Adjust
        }
        val updatedUser = user.copy(
            email = updateEmailRequestBody.emailAddress
        )

        userRepository.save(updatedUser)

        return updatedUser.toAccountDetailsResponse()
    }
}
