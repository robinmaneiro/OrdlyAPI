package com.robinmaneiro.order_kiosk_backend.menu.database

import com.robinmaneiro.order_kiosk_backend.menu.database.model.DbMenuCategory
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuCategoriesRepository: MongoRepository<DbMenuCategory, ObjectId> {
    // No custom methods are necessary
}
