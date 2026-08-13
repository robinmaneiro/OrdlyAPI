package com.robinmaneiro.ordly.api.menu.service.model

data class MenuProductsResponse(
    val itemCount: Int,
    val items: List<MenuSingleProductResponse>
)
