package com.robinmaneiro.order_kiosk_backend.database.repository

import com.robinmaneiro.order_kiosk_backend.database.model.RefreshGuestSession
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshGuestSessionRepository: MongoRepository<RefreshGuestSession, ObjectId> {
    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshGuestSession?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}
