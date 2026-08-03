package com.robinmaneiro.order_kiosk_backend.menu.service.model

typealias MenuCategoryResponse = List<MenuCategory>

data class MenuCategory(
    val id: String,
    val categoryName: String,
    val isSelected: Boolean,
    val imageUrl: String? = null
)
