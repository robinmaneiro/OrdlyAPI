package com.robinmaneiro.order_kiosk_backend.database.model

import org.bson.types.ObjectId

data class GuestSession(
    val id: ObjectId,
    val bagId: ObjectId,
    val wishlistId: ObjectId
)
