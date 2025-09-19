package com.robinmaneiro.order_kiosk_backend.bag.database

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("bag_items")
data class BagItem(
    @Id val id: ObjectId = ObjectId.get(),
    val productId: String,
    val quantity: Int,
    val title: String,
    val description: String,
    val price: Int,
    )