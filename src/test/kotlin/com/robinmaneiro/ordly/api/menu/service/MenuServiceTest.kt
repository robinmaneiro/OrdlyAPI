package com.robinmaneiro.ordly.api.menu.service

import com.robinmaneiro.ordly.api.menu.database.MenuCategoriesRepository
import com.robinmaneiro.ordly.api.menu.database.MenuProductsRepository
import com.robinmaneiro.ordly.api.menu.database.model.DbMenuCategory
import com.robinmaneiro.ordly.api.menu.database.model.DbMenuProduct
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

class MenuServiceTest {

    private val productsRepository = mockk<MenuProductsRepository>()
    private val categoriesRepository = mockk<MenuCategoriesRepository>()
    private val service = MenuService(categoriesRepository, productsRepository)

    private fun product(
        itemId: String = "p1",
        title: String = "Big Mac",
        description: String = "Two beef patties",
        price: Int = 500,
        categories: List<String> = listOf("cat-burgers"),
        imageUrl: String? = "http://img/bigmac.jpg",
    ) = DbMenuProduct(
        itemId = itemId,
        title = title,
        description = description,
        price = price,
        categories = categories,
        imageUrl = imageUrl,
    )

    @Test
    fun `fetchMenuProduct maps every field including imageUrl and derived price`() {
        every { productsRepository.findByItemId("p1") } returns Optional.of(product())

        val result = service.fetchMenuProduct("p1")

        requireNotNull(result)
        assertEquals("p1", result.id)
        assertEquals("Big Mac", result.title)
        assertEquals("Two beef patties", result.description)
        assertEquals(500, result.price.withTax)
        assertEquals(500, result.price.withoutTax)
        assertEquals(listOf("cat-burgers"), result.categories)
        assertEquals("http://img/bigmac.jpg", result.imageUrl)
    }

    @Test
    fun `fetchMenuProduct returns null when the product does not exist`() {
        every { productsRepository.findByItemId("missing") } returns Optional.empty()

        assertNull(service.fetchMenuProduct("missing"))
    }

    @Test
    fun `fetchProductsByCategory returns only products in that category with a matching count`() {
        val burger = product(itemId = "b1", categories = listOf("cat-burgers"))
        val burgerInTwoCats = product(itemId = "b2", categories = listOf("cat-burgers", "cat-deals"))
        val drink = product(itemId = "d1", categories = listOf("cat-drinks"))
        every { productsRepository.findAll() } returns mutableListOf(burger, burgerInTwoCats, drink)

        val result = service.fetchProductsByCategory("cat-burgers")

        assertEquals(2, result.itemCount)
        assertEquals(setOf("b1", "b2"), result.items.map { it.id }.toSet())
    }

    @Test
    fun `fetchAllMenuProducts returns every product and a matching count`() {
        every { productsRepository.findAll() } returns mutableListOf(product("a"), product("b"))

        val result = service.fetchAllMenuProducts()

        assertEquals(2, result.itemCount)
        assertEquals(2, result.items.size)
    }

    @Test
    fun `fetchAllMenuCategories maps isDefault to isSelected and exposes the hex id`() {
        val id = ObjectId()
        val category = DbMenuCategory(
            id = id,
            categoryName = "Burgers",
            isDefault = true,
            imageUrl = "http://img/burgers.jpg",
        )
        every { categoriesRepository.findAll() } returns mutableListOf(category)

        val result = service.fetchAllMenuCategories()

        assertEquals(1, result.size)
        assertEquals(id.toHexString(), result[0].id)
        assertEquals("Burgers", result[0].categoryName)
        assertTrue(result[0].isSelected)
        assertEquals("http://img/burgers.jpg", result[0].imageUrl)
    }
}
