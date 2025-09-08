package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.MenuProduct
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface MenuProductsRepository: MongoRepository<MenuProduct, ObjectId> {
    fun findByItemId(itemId: String): Optional<MenuProduct>
}
