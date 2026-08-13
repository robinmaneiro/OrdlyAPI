package com.robinmaneiro.ordly.api.bag.database

import com.robinmaneiro.ordly.api.bag.database.model.DbBag
import com.robinmaneiro.ordly.api.bag.database.model.DbBagItem
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface BagRepository : MongoRepository<DbBag, ObjectId> {
    // Nothing extra to add
}