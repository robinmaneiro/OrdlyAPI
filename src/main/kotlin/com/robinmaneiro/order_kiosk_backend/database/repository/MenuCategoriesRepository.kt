package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.MenuCategory
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuCategoriesRepository: MongoRepository<MenuCategory, ObjectId> {
    // No custom methods are necessary
}
