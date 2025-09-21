package com.robinmaneiro.order_kiosk_backend.menu.service.model

data class MenuSingleProductResponse(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val categories: List<String>
)
