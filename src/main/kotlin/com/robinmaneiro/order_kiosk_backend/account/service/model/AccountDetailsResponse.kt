package com.robinmaneiro.order_kiosk_backend.account.service.model

data class AccountDetailsResponse(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: String?,
    val phone: String
)