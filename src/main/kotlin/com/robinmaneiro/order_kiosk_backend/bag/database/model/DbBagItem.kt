package com.robinmaneiro.order_kiosk_backend.bag.database.model

import com.robinmaneiro.order_kiosk_backend.bag.service.model.ItemPrice
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("bag_items")
data class DbBagItem(
    @Id val id: ObjectId = ObjectId.get(),
    val productId: String,
    val quantity: Int,
    val title: String,
    val description: String,
    val price: ItemPrice,
    )