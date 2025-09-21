package com.robinmaneiro.order_kiosk_backend.menu.service.model

data class MenuProductsResponse(
    val itemCount: Int,
    val items: List<MenuSingleProductResponse>
)
