package com.robinmaneiro.ordly.api.menu.service

import com.robinmaneiro.ordly.api.menu.database.MenuCategoriesRepository
import com.robinmaneiro.ordly.api.menu.database.MenuProductsRepository
import com.robinmaneiro.ordly.api.menu.database.model.DbMenuProduct
import com.robinmaneiro.ordly.api.menu.service.model.MenuCategory
import com.robinmaneiro.ordly.api.menu.service.model.MenuProductsResponse
import com.robinmaneiro.ordly.api.menu.service.model.MenuSingleProductResponse
import com.robinmaneiro.ordly.api.menu.service.model.PriceData
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MenuService(
    private val menuCategoriesRepository: MenuCategoriesRepository,
    private val menuProductsRepository: MenuProductsRepository
) {
    private fun DbMenuProduct.asMenuSingleProduct() =  MenuSingleProductResponse(
            id = itemId,
            title = title,
            description = description,
            price = PriceData(
                withTax = price,
                withoutTax = price
            ),
            categories = categories,
            imageUrl = imageUrl)

    fun fetchAllMenuCategories(): List<MenuCategory> {
        return menuCategoriesRepository.findAll().map {
            MenuCategory(
                id = it.id.toHexString(),
                categoryName = it.categoryName,
                isSelected = it.isDefault,
                imageUrl = it.imageUrl
            )
        }
    }

    fun fetchProductsByCategory(categoryId: String): MenuProductsResponse {
        return MenuProductsResponse(
            itemCount = menuProductsRepository.findAll().count { categoryId in it.categories },
            items = menuProductsRepository.findAll().filter { categoryId in it.categories }.map {
                it.asMenuSingleProduct()
            }
        )
    }

    fun fetchAllMenuProducts(): MenuProductsResponse {
        return MenuProductsResponse(
            itemCount = menuProductsRepository.findAll().count(),
            items = menuProductsRepository.findAll().map { it.asMenuSingleProduct() }
        )
    }

    fun fetchMenuProduct(productId: String): MenuSingleProductResponse? {
        return menuProductsRepository.findByItemId(productId).getOrNull()?.asMenuSingleProduct()
    }
}
