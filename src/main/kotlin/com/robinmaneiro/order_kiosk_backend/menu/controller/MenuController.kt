package com.robinmaneiro.order_kiosk_backend.menu.controller

import com.robinmaneiro.order_kiosk_backend.menu.service.MenuService
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuCategoryResponse
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuProductsResponse
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuSingleProductResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/menu")
class MenuController(
    private val menuService: MenuService
) {

    @GetMapping("/categories")
    fun fetchAllMenuCategories(): MenuCategoryResponse {
        return menuService.fetchAllMenuCategories()
    }

    @GetMapping("/categories/{categoryId}")
    fun fetchProductsByCategory(@PathVariable categoryId: String): MenuProductsResponse {
        return menuService.fetchProductsByCategory(categoryId)
    }

    @GetMapping("/items/all")
    fun fetchAllMenuProducts(): MenuProductsResponse {
        return menuService.fetchAllMenuProducts()
    }

    @GetMapping("/items/{productId}")
    fun fetchMenuProduct(@PathVariable productId: String): MenuSingleProductResponse? {
        return menuService.fetchMenuProduct(productId)
    }
}
