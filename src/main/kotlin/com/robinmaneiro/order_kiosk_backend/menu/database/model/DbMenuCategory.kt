package com.robinmaneiro.order_kiosk_backend.menu.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("categories")
data class DbMenuCategory(
    @Id val id: ObjectId = ObjectId.get(),
    val categoryName: String,
    val isDefault: Boolean = false,
    val imageUrl: String? = null
)
