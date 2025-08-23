package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("categories")
data class MenuCategory(
    @Id val categoryId: ObjectId = ObjectId.get(), //TODO: Study the effects of not setting a default
    val categoryName: String
)
