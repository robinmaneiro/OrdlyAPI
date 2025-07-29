package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document("items")
data class MenuItem(
    @Id val id: ObjectId = ObjectId.get(),
    val title: String,
    val price: Double,
)
