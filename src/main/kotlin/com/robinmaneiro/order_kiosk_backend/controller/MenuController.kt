package com.robinmaneiro.order_kiosk_backend.controller

import com.robinmaneiro.order_kiosk_backend.database.repository.MenuCategoriesRepository
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuProductsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/menu")
class MenuController(
    private val menuProductsRepository: MenuProductsRepository,
    private val menuCategoriesRepository: MenuCategoriesRepository
) {
    data class MenuItemsResponse(
        val itemCount: Int,
        val items: List<MenuSingleItemResponse>
    )

    data class MenuSingleItemResponse(
        val id: String,
        val title: String,
        val description: String,
        val price: Int,
        val categories: List<String>
    )

    data class MenuCategoryResponse(
        val id: String,
        val categoryName: String,
        val isSelected: Boolean
    )

    @GetMapping("/categories")
    fun getAllMenuCategories(): List<MenuCategoryResponse> {
        return menuCategoriesRepository.findAll().map {
            MenuCategoryResponse(
                id = it.id.toHexString(),
                categoryName = it.categoryName,
                isSelected = it.isDefault
            )
        }
    }

    @GetMapping("/categories/{categoryId}")
    fun getItemsByCategory(@PathVariable categoryId: String): MenuItemsResponse {
        return MenuItemsResponse(
            itemCount = menuProductsRepository.findAll().count { categoryId in it.categories },
            items = menuProductsRepository.findAll().filter { categoryId in it.categories }.map {
                MenuSingleItemResponse(
                    id = it.itemId,
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    categories = it.categories
                )
            }
        )
    }

    @GetMapping("/items/all")
    fun getAllMenuItems(): MenuItemsResponse {
        return MenuItemsResponse(
            itemCount = menuProductsRepository.findAll().count(),
            items = menuProductsRepository.findAll().map {
                MenuSingleItemResponse(
                    id = it.id.toHexString(),
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    categories = it.categories
                )
            }
        )
    }

    @GetMapping("/items/{itemId}")
    fun getMenuItem(@PathVariable itemId: String): MenuSingleItemResponse? {
        return menuProductsRepository.findByItemId(itemId).getOrNull()?.let {
            MenuSingleItemResponse(
                id = it.itemId,
                title = it.title,
                description = it.description,
                price = it.price,
                categories = it.categories
            )
        }
    }
}
