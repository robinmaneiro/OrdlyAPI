package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
    @Id val id: ObjectId = ObjectId(),
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hashedPassword: String,
    val dateOfBirth: String,
    val phone: String,
    val bagId: ObjectId,
    val wishlistId: ObjectId
)
