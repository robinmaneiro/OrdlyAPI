package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("products")
data class MenuProduct(
    @Id val id: ObjectId = ObjectId.get(),
    val itemId: String,
    val title: String,
    val description: String,
    val price: Double,
    val categories: List<String>
)
