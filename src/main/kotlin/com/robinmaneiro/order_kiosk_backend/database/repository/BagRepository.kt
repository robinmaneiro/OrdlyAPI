package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.BagItem
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BagRepository: MongoRepository<BagItem, ObjectId>
