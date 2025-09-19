package com.robinmaneiro.order_kiosk_backend.bag.database

import com.robinmaneiro.order_kiosk_backend.bag.database.model.DbBagItem
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BagRepository : MongoRepository<DbBagItem, ObjectId> {
    // Nothing extra to add
}