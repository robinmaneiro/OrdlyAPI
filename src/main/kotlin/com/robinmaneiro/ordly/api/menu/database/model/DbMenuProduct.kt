package com.robinmaneiro.ordly.api.menu.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("products")
data class DbMenuProduct(
    @Id val id: ObjectId = ObjectId.get(),
    val itemId: String,
    val title: String,
    val description: String,
    val price: Int,
    val categories: List<String>,
    val imageUrl: String? = null
)
