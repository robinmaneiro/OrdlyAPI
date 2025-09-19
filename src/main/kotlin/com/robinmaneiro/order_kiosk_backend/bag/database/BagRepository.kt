package com.robinmaneiro.order_kiosk_backend.bag.database

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BagRepository : MongoRepository<BagItem, ObjectId> {
    // Nothing extra to define
}