package com.robinmaneiro.order_kiosk_backend.menu.service

import com.robinmaneiro.order_kiosk_backend.menu.database.MenuCategoriesRepository
import com.robinmaneiro.order_kiosk_backend.menu.database.MenuProductsRepository
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuCategory
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuCategoryResponse
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuProductsResponse
import com.robinmaneiro.order_kiosk_backend.menu.service.model.MenuSingleProductResponse
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MenuService(
    private val menuCategoriesRepository: MenuCategoriesRepository,
    private val menuProductsRepository: MenuProductsRepository
) {
    fun fetchAllMenuCategories(): MenuCategoryResponse {
        return menuCategoriesRepository.findAll().map {
            MenuCategory(
                id = it.id.toHexString(),
                categoryName = it.categoryName,
                isSelected = it.isDefault
            )
        }
    }

    fun fetchProductsByCategory(categoryId: String): MenuProductsResponse {
        return MenuProductsResponse(
            itemCount = menuProductsRepository.findAll().count { categoryId in it.categories },
            items = menuProductsRepository.findAll().filter { categoryId in it.categories }.map {
                MenuSingleProductResponse(
                    id = it.itemId,
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    categories = it.categories
                )
            }
        )
    }

    fun fetchAllMenuProducts(): MenuProductsResponse {
        return MenuProductsResponse(
            itemCount = menuProductsRepository.findAll().count(),
            items = menuProductsRepository.findAll().map {
                MenuSingleProductResponse(
                    id = it.id.toHexString(),
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    categories = it.categories
                )
            }
        )
    }

    fun fetchMenuProduct(productId: String): MenuSingleProductResponse? {
        return menuProductsRepository.findByItemId(productId).getOrNull()?.let {
            MenuSingleProductResponse(
                id = it.itemId,
                title = it.title,
                description = it.description,
                price = it.price,
                categories = it.categories
            )
        }
    }
}
