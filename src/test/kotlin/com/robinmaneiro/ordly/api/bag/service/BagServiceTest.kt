package com.robinmaneiro.ordly.api.bag.service

import com.robinmaneiro.ordly.api.bag.controller.BagController
import com.robinmaneiro.ordly.api.bag.database.BagRepository
import com.robinmaneiro.ordly.api.bag.database.model.DbBag
import com.robinmaneiro.ordly.api.bag.database.model.DbBagItem
import com.robinmaneiro.ordly.api.bag.service.model.ItemPrice
import com.robinmaneiro.ordly.api.bag.service.model.PriceData
import com.robinmaneiro.ordly.api.menu.database.MenuProductsRepository
import com.robinmaneiro.ordly.api.menu.database.model.DbMenuProduct
import com.robinmaneiro.ordly.api.util.errorhandling.ProductNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.Optional

class BagServiceTest {

    private val bagRepository = mockk<BagRepository>()
    private val productsRepository = mockk<MenuProductsRepository>()
    private val service = BagService(bagRepository, productsRepository)

    private val bagObjectId = ObjectId()
    private val bagId = bagObjectId.toHexString()

    private fun itemPrice(unit: Int, quantity: Int) = ItemPrice(
        unit = PriceData(withTax = unit, withoutTax = unit),
        total = PriceData(withTax = unit * quantity, withoutTax = unit * quantity),
    )

    private fun bagItem(
        id: ObjectId = ObjectId(),
        productId: String = "p1",
        quantity: Int = 1,
        unitPrice: Int = 500,
    ) = DbBagItem(
        id = id,
        productId = productId,
        quantity = quantity,
        title = "Item",
        description = "desc",
        price = itemPrice(unitPrice, quantity),
    )

    private fun emptyBag() = DbBag(
        id = bagObjectId,
        createdAt = Instant.now(),
        items = emptyList(),
        bagType = "GUEST",
    )

    /** Simple in-memory store so save() updates what a later findById() returns. */
    private fun stubSingleBagStore(initial: DbBag) {
        var current = initial
        every { bagRepository.findById(bagObjectId) } answers { Optional.of(current) }
        every { bagRepository.save(any()) } answers { current = firstArg(); current }
    }

    @Test
    fun `fetchBag sums line totals and item quantities`() {
        val bag = emptyBag().copy(
            items = listOf(
                bagItem(productId = "a", quantity = 2, unitPrice = 300), // line total 600
                bagItem(productId = "b", quantity = 1, unitPrice = 450), // line total 450
            ),
        )
        every { bagRepository.findById(bagObjectId) } returns Optional.of(bag)

        val response = service.fetchBag(bagId)

        assertEquals(1050, response.totalCost.withTax)
        assertEquals(1050, response.totalCost.withoutTax)
        assertEquals(3, response.itemCount)
        assertEquals(2, response.items.size)
    }

    @Test
    fun `fetchBag throws when the bag does not exist`() {
        every { bagRepository.findById(bagObjectId) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { service.fetchBag(bagId) }
    }

    @Test
    fun `addItemToBag sets the line total to unit price times quantity and copies the image`() {
        stubSingleBagStore(emptyBag())
        every { productsRepository.findByItemId("p1") } returns Optional.of(
            DbMenuProduct(
                itemId = "p1",
                title = "Fries",
                description = "Golden fries",
                price = 250,
                categories = listOf("sides"),
                imageUrl = "http://img/fries.jpg",
            ),
        )

        val response = service.addItemToBag(bagId, BagController.AddToBagRequest("p1", 3))

        assertEquals(1, response.items.size)
        val line = response.items.first()
        assertEquals(3, line.quantity)
        assertEquals(250, line.price.unit.withTax)
        assertEquals(750, line.price.total.withTax)
        assertEquals("http://img/fries.jpg", line.imageUrl)
        assertEquals(750, response.totalCost.withTax)
    }

    @Test
    fun `addItemToBag throws ProductNotFoundException for an unknown product`() {
        stubSingleBagStore(emptyBag())
        every { productsRepository.findByItemId("nope") } returns Optional.empty()

        assertThrows<ProductNotFoundException> {
            service.addItemToBag(bagId, BagController.AddToBagRequest("nope", 1))
        }
    }

    @Test
    fun `patchBagItem updates the quantity and recomputes the line total`() {
        val item = bagItem(productId = "p1", quantity = 1, unitPrice = 500)
        stubSingleBagStore(emptyBag().copy(items = listOf(item)))

        val response = service.patchBagItem(bagId, item.id.toHexString(), 4)

        val line = response.items.first()
        assertEquals(4, line.quantity)
        assertEquals(2000, line.price.total.withTax)
        assertEquals(2000, response.totalCost.withTax)
        assertEquals(4, response.itemCount)
    }

    @Test
    fun `patchBagItem throws for an unknown item id`() {
        stubSingleBagStore(emptyBag().copy(items = listOf(bagItem())))

        assertThrows<IllegalArgumentException> {
            service.patchBagItem(bagId, ObjectId().toHexString(), 2)
        }
    }

    @Test
    fun `deleteAllBagItems empties the bag`() {
        stubSingleBagStore(emptyBag().copy(items = listOf(bagItem(), bagItem())))

        val response = service.deleteAllBagItems(bagId)

        assertTrue(response.items.isEmpty())
        assertEquals(0, response.itemCount)
        assertEquals(0, response.totalCost.withTax)
    }

    @Test
    fun `mergeBags combines quantities for shared products and unions the rest`() {
        val sourceId = ObjectId()
        val targetId = ObjectId()
        val source = DbBag(
            id = sourceId,
            createdAt = Instant.now(),
            bagType = "GUEST",
            items = listOf(
                bagItem(productId = "shared", quantity = 2, unitPrice = 100),
                bagItem(productId = "only-source", quantity = 1, unitPrice = 100),
            ),
        )
        val target = DbBag(
            id = targetId,
            createdAt = Instant.now(),
            bagType = "USER",
            items = listOf(
                bagItem(productId = "shared", quantity = 3, unitPrice = 100),
                bagItem(productId = "only-target", quantity = 5, unitPrice = 100),
            ),
        )
        every { bagRepository.findById(sourceId) } returns Optional.of(source)
        every { bagRepository.findById(targetId) } returns Optional.of(target)
        every { bagRepository.save(any()) } answers { firstArg() }

        val response = service.mergeBags(sourceId.toHexString(), targetId.toHexString())

        val byProduct = response.items.associateBy { it.productId }
        assertEquals(3, response.items.size)
        assertEquals(5, byProduct.getValue("shared").quantity)
        assertEquals(1, byProduct.getValue("only-source").quantity)
        assertEquals(5, byProduct.getValue("only-target").quantity)
    }
}
