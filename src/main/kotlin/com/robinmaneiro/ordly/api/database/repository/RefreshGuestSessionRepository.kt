package com.robinmaneiro.ordly.api.database.repository

import com.robinmaneiro.ordly.api.database.model.RefreshGuestSession
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshGuestSessionRepository: MongoRepository<RefreshGuestSession, ObjectId> {
    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshGuestSession?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}
