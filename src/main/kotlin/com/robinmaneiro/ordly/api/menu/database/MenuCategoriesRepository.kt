package com.robinmaneiro.ordly.api.menu.database

import com.robinmaneiro.ordly.api.menu.database.model.DbMenuCategory
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuCategoriesRepository: MongoRepository<DbMenuCategory, ObjectId> {
    // No custom methods are necessary
}
