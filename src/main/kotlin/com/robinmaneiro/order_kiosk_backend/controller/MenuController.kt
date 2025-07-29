package com.robinmaneiro.order_kiosk_backend.controller

import com.robinmaneiro.order_kiosk_backend.database.model.MenuItem
import com.robinmaneiro.order_kiosk_backend.database.repository.MenuRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/menu_items")
class MenuController(
    private val menuRepository: MenuRepository
) {
    data class MenuItemsResponse(
        val title: String,
        val price: Double
    )

    @GetMapping
    fun getAllMenuItems(): List<MenuItemsResponse> {
        return menuRepository.findAll().map {
            MenuItemsResponse(
                title = it.title,
                price = it.price
            )
        }
    }
}
