package com.robinmaneiro.ordly.api.database.repository

import com.robinmaneiro.ordly.api.database.model.GuestSession
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface GuestSessionRepository: MongoRepository<GuestSession, ObjectId> {
    // Do nothing
}
