package com.robinmaneiro.ordly.api.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document("guest_session")
data class GuestSession(
    val id: ObjectId,
    val bagId: ObjectId,
    val wishlistId: ObjectId
)
