package com.robinmaneiro.ordly.api.menu.service.model

typealias MenuCategoryResponse = List<MenuCategory>

data class MenuCategory(
    val id: String,
    val categoryName: String,
    val isSelected: Boolean,
    val imageUrl: String? = null
)
