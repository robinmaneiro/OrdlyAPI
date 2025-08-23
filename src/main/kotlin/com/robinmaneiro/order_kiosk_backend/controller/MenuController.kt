package com.robinmaneiro.order_kiosk_backend.controller

import com.robinmaneiro.order_kiosk_backend.database.repository.MenuCategoriesRepository
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuItemsRepository
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/menu")
class MenuController(
    private val menuItemsRepository: MenuItemsRepository,
    private val menuCategoriesRepository: MenuCategoriesRepository
) {
    data class MenuItemsResponse(
        val id: String,
        val title: String,
        val description: String,
        val price: Double,
        val categories: List<String>
    )

    data class MenuCategoryResponse(
        val categoryId: String,
        val categoryName: String
    )

    @GetMapping("/categories")
    fun getAllMenuCategories(): List<MenuCategoryResponse> {
        return menuCategoriesRepository.findAll().map {
            MenuCategoryResponse(
                categoryId = it.categoryId.toHexString(),
                categoryName = it.categoryName
            )
        }
    }

    @GetMapping("/items")
    fun getAllMenuItems(): List<MenuItemsResponse> {
        return menuItemsRepository.findAll().map {
            MenuItemsResponse(
                id = it.id.toHexString(),
                title = it.title,
                description = it.description,
                price = it.price,
                categories = it.categories
            )
        }
    }

    @GetMapping("/items/{id}")
    fun getMenuItem(@PathVariable id: String): MenuItemsResponse? {
        return menuItemsRepository.findByIdOrNull(ObjectId(id))?.let {
            MenuItemsResponse(
                id = it.id.toHexString(),
                title = it.title,
                description = it.description,
                price = it.price,
                categories = it.categories
            )
        }
    }
}
