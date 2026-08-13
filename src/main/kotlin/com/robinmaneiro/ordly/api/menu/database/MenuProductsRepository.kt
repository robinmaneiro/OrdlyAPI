package com.robinmaneiro.ordly.api.menu.database

import com.robinmaneiro.ordly.api.menu.database.model.DbMenuProduct
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface MenuProductsRepository: MongoRepository<DbMenuProduct, ObjectId> {
    fun findByItemId(itemId: String): Optional<DbMenuProduct>
}
