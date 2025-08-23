package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.MenuItem
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuItemsRepository: MongoRepository<MenuItem, ObjectId> {
    // No custom methods are necessary
}
