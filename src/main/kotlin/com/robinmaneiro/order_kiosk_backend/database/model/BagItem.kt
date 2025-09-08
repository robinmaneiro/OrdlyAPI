package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("bag_items")
data class BagItem(
    @Id val id: ObjectId = ObjectId.get(),
    val productId: String,
    val quantity: Int
)
