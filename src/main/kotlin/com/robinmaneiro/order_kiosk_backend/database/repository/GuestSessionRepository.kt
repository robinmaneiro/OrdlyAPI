package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.GuestSession
import com.robinmaneiro.order_kiosk_backend.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface GuestSessionRepository: MongoRepository<GuestSession, ObjectId> {
    // Do nothing
}
